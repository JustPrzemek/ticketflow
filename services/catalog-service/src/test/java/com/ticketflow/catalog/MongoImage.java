package com.ticketflow.catalog;

/**
 * MongoDB image used by tests. Keep in sync with {@code compose.yaml}.
 * <p>
 * MongoDB 8.0 refuses to start on Linux kernels 6.19+ (SERVER-121912), which GitHub runners
 * and current desktop distributions use — hence 9.0.
 */
final class MongoImage {

    static final String NAME = "mongo:9.0";

    private MongoImage() {
    }
}
