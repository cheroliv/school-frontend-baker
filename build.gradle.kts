import Build_gradle.RepositoryConfiguration.Companion.CNAME
import Build_gradle.RepositoryConfiguration.Companion.ORIGIN
import Build_gradle.RepositoryConfiguration.Companion.REMOTE
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Git.init
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.PushResult
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.IOException
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.FileSystems.getDefault

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        val jacksonVersion = "2.15.2"
        val jgitVersion = "6.8.0.202311291450-r"
        val slf4jVersion = "2.0.17"
        val commonIOVersion = "2.19.0"
        val jbakeVersion = "5.5.0"
        val tukaaniVersion = "1.10"
        val deps = listOf(
            "org.jbake:jbake-gradle-plugin:$jbakeVersion",
            "org.slf4j:slf4j-simple:$slf4jVersion",
            "commons-io:commons-io:$commonIOVersion",
            "com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion",
            "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion",
            "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion",
            "org.eclipse.jgit:org.eclipse.jgit:$jgitVersion",
            "org.eclipse.jgit:org.eclipse.jgit.archive:$jgitVersion",
            "org.eclipse.jgit:org.eclipse.jgit.ssh.jsch:$jgitVersion",
            "org.tukaani:xz:$tukaaniVersion",
        )
        deps.map { classpath(it) }
    }
}

plugins { id("org.jbake.site") }

//TODO: add readme-site-repository
tasks.register("publishSite") {
    group = "managed"
    description = "Publish site online."
    dependsOn("bake")
    doFirst { createCnameFile() }
    jbake {
        srcDirName = bakeSrcPath
        destDirName = bakeDestDirPath
    }
    doLast {
        pushPages(
            destPath = { "${layout.buildDirectory.get().asFile.absolutePath}${getDefault().separator}$bakeDestDirPath" },
            pathTo = { "${layout.buildDirectory.get().asFile.absolutePath}${getDefault().separator}${localConf.pushPage.to}" }
        )
    }
}

data class GitPushConfiguration(
    val from: String,
    val to: String,
    val repo: RepositoryConfiguration,
    val branch: String,
    val message: String,
)

data class RepositoryConfiguration(
    val name: String,
    val repository: String,
    val credentials: RepositoryCredentials,
) {
    companion object {
        const val ORIGIN = "origin"
        const val CNAME = "CNAME"
        const val REMOTE = "remote"
    }
}

data class RepositoryCredentials(val username: String, val password: String)

data class SiteConfiguration(
    val bake: BakeConfiguration,
    val pushPage: GitPushConfiguration,
    val pushSource: GitPushConfiguration? = null,
    val pushTemplate: GitPushConfiguration? = null,
)

data class BakeConfiguration(
    val srcPath: String,
    val destDirPath: String,
    val cname: String?,
)

sealed class FileOperationResult {
    sealed class GitOperationResult {
        data class Success(
            val commit: RevCommit, val pushResults: MutableIterable<PushResult>?
        ) : GitOperationResult()

        data class Failure(val error: String) : GitOperationResult()
    }

    object Success : FileOperationResult()
    data class Failure(val error: String) : FileOperationResult()
}

/*=================================================================================*/

val mapper: ObjectMapper
    get() = YAMLFactory()
        .let(::ObjectMapper)
        .disable(WRITE_DATES_AS_TIMESTAMPS)
        .registerKotlinModule()

/*=================================================================================*/

val localConf: SiteConfiguration
    get() = readSiteConfigurationFile {
        "$rootDir${getDefault().separator}${properties["managed_config_path"]}"
    }

fun readSiteConfigurationFile(
    configPath: () -> String
): SiteConfiguration = try {
    configPath().let(::File).let(mapper::readValue)
} catch (e: Exception) {
// Handle exception or log error
    SiteConfiguration(
        BakeConfiguration("", "", null),
        GitPushConfiguration(
            "", "", RepositoryConfiguration(
                "",
                "",
                RepositoryCredentials("", "")
            ), "", ""
        )
    )
}

/*=================================================================================*/

val bakeSrcPath: String get() = localConf.bake.srcPath

val bakeDestDirPath: String get() = localConf.bake.destDirPath

/*=================================================================================*/

fun createCnameFile() {
    logger.run {
        when {
            localConf.bake.cname != null && localConf.bake.cname!!.isNotBlank() -> file(
                "${project.layout.buildDirectory.get().asFile.absolutePath}${
                    getDefault().separator
                }${localConf.bake.destDirPath}${getDefault().separator}$CNAME"
            ).run {
                if (exists() && isDirectory) {
                    info("$name exists as directory.")
                    deleteRecursively()
                } else info("$name does not exist as directory.")
                if (exists()) {
                    info("$name successfully deleted.")
                    delete()
                } else info("$name does not exist as file.")
                if (exists()) throw "$name file should not exist."
                    .run(::IOException)
                else info("$name does not exist as file or directory.")
                if (!createNewFile()) throw "Cannot create $name file."
                    .run(::IOException)
                else info("$name successfully created.")
                if (!exists()) throw "$name file should exist.".run(::IOException)
                else info("$name file exists.")
                appendText(localConf.bake.cname ?: "", UTF_8)
                if (!(exists() && !isDirectory)) throw "$name file should exist as file not as directory."
                    .run(::IOException) else info("$name file exists as file and not directory.")
            }
        }
    }
}

/*=================================================================================*/

fun createRepoDir(path: String): File = path
    .let(::File)
    .apply {
        if (exists() && !isDirectory) when {
            delete() -> logger.info("$name exists as file and successfully deleted.")
            else -> throw "$name exists and must be a directory".run(::IOException)
        }

        if (exists()) when {
            deleteRecursively() -> logger.info("$name exists as directory and successfully deleted.")
            else -> throw "$name exists as a directory and cannot be deleted".run(::IOException)
        }
        when {
            !exists() -> logger.info("$name does not exist.")
            else -> throw IOException("$name must not exist anymore.")
        }
        if (!exists()) {
            if (mkdir()) logger.info("$name as directory successfully created.")
            else throw IOException("$name as directory cannot be created.")
        }
    }

/*=================================================================================*/

fun copyBakedFilesToRepo(
    bakeDirPath: String, repoDir: File
): FileOperationResult = try {
    bakeDirPath
        .also { "bakeDirPath : $it".let(logger::info) }
        .let(::File)
        .apply {
            copyRecursively(repoDir, true)
            deleteRecursively()
        }.run {
            when {
                !exists() -> logger.info("$name directory successfully deleted.")
                else -> throw IOException("$name must not exist.")
            }
        }
    FileOperationResult.Success
} catch (e: Exception) {
    FileOperationResult.Failure(e.message ?: "An error occurred during file copy.")
}

/*=================================================================================*/

fun initAddCommit(
    repoDir: File,
    conf: SiteConfiguration,
): RevCommit {
    //3) initialiser un repo dans le dossier cvs
    init().setDirectory(repoDir).call().run {
        if (!repository.isBare) logger.info("Repository is not bare")
        else throw Exception("Repository is bare")
        if (repository.directory.isDirectory) logger.info("Repository file is a directory")
        else throw Exception("Repository file must be a directory")
        // add remote repo:
        remoteAdd().apply {
            setName(ORIGIN)
            setUri(URIish(conf.pushPage.repo.repository))
            // you can add more settings here if needed
        }.call()
        //4) ajouter les fichiers du dossier cvs à l'index
        add().addFilepattern(".").call()
        //5) commit
        return commit().setMessage(conf.pushPage.message).call()
    }
}

/*=================================================================================*/


fun push(repoDir: File, conf: SiteConfiguration): MutableIterable<PushResult>? =
    FileRepositoryBuilder().setGitDir(
        "${repoDir.absolutePath}${getDefault().separator}.git".let(::File)
    ).readEnvironment()
        .findGitDir()
        .setMustExist(true)
        .build()
        .also {
            it.config.apply {
                getString(
                    REMOTE,
                    ORIGIN,
                    conf.pushPage.repo.repository
                )
            }.save()
            when {
                !it.isBare -> logger.info("$it repository is not bare.")
                else -> throw Exception("$it must not be bare.")
            }
        }.let(::Git).run {
            // push to remote:
            push().apply {
                setCredentialsProvider(
                    UsernamePasswordCredentialsProvider(
                        conf.pushPage.repo.credentials.username,
                        conf.pushPage.repo.credentials.password
                    )
                )
                //you can add more settings here if needed
                remote = ORIGIN
                isForce = true
            }.call()
        }

/*=================================================================================*/


fun pushPages(
    destPath: () -> String,
    pathTo: () -> String
) = pathTo()
    .run(::createRepoDir)
    .let { it: File ->
        copyBakedFilesToRepo(destPath(), it)
            .takeIf { it is FileOperationResult.Success }
            ?.run {
                initAddCommit(it, localConf)
                push(it, localConf)
                it.deleteRecursively()
                destPath().let(::File).deleteRecursively()
            }
    }

/*=================================================================================*/
