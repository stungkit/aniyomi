package mihon.data.repository.anime

import android.database.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mihon.domain.extensionrepo.anime.repository.AnimeExtensionRepoRepository
import mihon.domain.extensionrepo.exception.SaveExtensionRepoException
import mihon.domain.extensionrepo.model.ExtensionRepo
import tachiyomi.data.handlers.anime.AnimeDatabaseHandler

class AnimeExtensionRepoRepositoryImpl(
    private val handler: AnimeDatabaseHandler,
) : AnimeExtensionRepoRepository {
    override fun subscribeAll(): Flow<List<ExtensionRepo>> {
        return handler.subscribeToList { extension_reposQueries.findAll(::mapExtensionRepo) }
    }

    override suspend fun getAll(): List<ExtensionRepo> {
        return handler.awaitList { extension_reposQueries.findAll(::mapExtensionRepo) }
    }

    override suspend fun getRepository(baseUrl: String): ExtensionRepo? {
        return handler.awaitOneOrNull { extension_reposQueries.findOne(baseUrl, ::mapExtensionRepo) }
    }

    override suspend fun getRepositoryBySigningKeyFingerprint(fingerprint: String): ExtensionRepo? {
        return handler.awaitOneOrNull {
            extension_reposQueries.findOneBySigningKeyFingerprint(fingerprint, ::mapExtensionRepo)
        }
    }

    override fun getCount(): Flow<Int> {
        return handler.subscribeToOne { extension_reposQueries.count() }.map { it.toInt() }
    }

    override suspend fun insertRepository(
        baseUrl: String,
        name: String,
        shortName: String?,
        website: String,
        signingKeyFingerprint: String,
    ) {
        try {
            handler.await { extension_reposQueries.insert(baseUrl, name, shortName, website, signingKeyFingerprint) }
        } catch (ex: SQLiteException) {
            throw SaveExtensionRepoException(ex)
        }
    }

    override suspend fun upsertRepository(
        baseUrl: String,
        name: String,
        shortName: String?,
        website: String,
        signingKeyFingerprint: String,
    ) {
        try {
            handler.await { extension_reposQueries.upsert(baseUrl, name, shortName, website, signingKeyFingerprint) }
        } catch (ex: SQLiteException) {
            throw SaveExtensionRepoException(ex)
        }
    }

    override suspend fun replaceRepository(newRepo: ExtensionRepo) {
        handler.await {
            extension_reposQueries.replace(
                newRepo.baseUrl,
                newRepo.name,
                newRepo.shortName,
                newRepo.website,
                newRepo.signingKeyFingerprint,
            )
        }
    }

    override suspend fun deleteRepository(baseUrl: String) {
        return handler.await { extension_reposQueries.delete(baseUrl) }
    }

    private fun mapExtensionRepo(
        baseUrl: String,
        name: String,
        shortName: String?,
        website: String,
        signingKeyFingerprint: String,
    ): ExtensionRepo = ExtensionRepo(
        baseUrl = baseUrl,
        name = name,
        shortName = shortName,
        website = website,
        signingKeyFingerprint = signingKeyFingerprint,
    )
}