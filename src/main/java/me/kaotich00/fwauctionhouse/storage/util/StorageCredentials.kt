package me.kaotich00.fwauctionhouse.storage.util

class StorageCredentials(
    val host: String,
    val database: String,
    val username: String,
    val password: String
) {

    class Builder internal constructor() {

        private lateinit var host: String

        private lateinit var database: String

        private lateinit var username: String

        private lateinit var password: String


        fun host(host: String): Builder {
            this.host = host
            return this
        }

        fun database(database: String): Builder {
            this.database = database
            return this
        }

        fun username(username: String): Builder {
            this.username = username
            return this
        }

        fun password(password: String): Builder {
            this.password = password
            return this
        }

        fun build() = StorageCredentials(host, database, username, password)
    }

    companion object {

        fun builder() = Builder()
    }

    /*

    fun toConnection(): Connection = DriverManager.getConnection(
        "jdbc:mysql://$host/$database?useSSL=false",
        username,
        password
    )

    companion object {

        const val MAX_POOL_SIZE = 10

        const val MIN_IDLE_CONNECTIONS = 10

        const val MAX_LIFETIME = 1800000

        const val CONNECTION_TIMEOUT = 5000
    }

    */
}