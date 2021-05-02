plugins {
    `maven-publish`
    kotlin("jvm")
}

dependencies {

    implementation(Libs.Exposed.core)
    implementation(Libs.Exposed.dao)
    implementation(Libs.Exposed.jdbc)
    implementation(Libs.Exposed.javaTime)

    implementation(Libs.hikariCP)
    implementation(Libs.slf4j)
    implementation(Libs.mySqlConnector)
}
