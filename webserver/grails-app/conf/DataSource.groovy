environments {
    development {
        grails {
            mongo {
                host = "localhost"
                databaseName = "mb_oauth"
            }
        }
    }
    test {
        grails {
            mongo {
                host = "localhost"
                databaseName = "mb_oauth"
            }
        }
    }
    production {
        grails {
            mongo {

                // replicaSet = []
                host = "localhost"
                username = ""
                password = ""
                databaseName = "mb_oauth"
            }
        }
    }
}