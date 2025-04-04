FROM mongo:8.0.6-noble


ARG INIT_DIR
ARG DB_NAME

ENV MONGO_INITDB_DATABASE=${DB_NAME}


COPY ${INIT_DIR} docker-entrypoint-initdb.d/
