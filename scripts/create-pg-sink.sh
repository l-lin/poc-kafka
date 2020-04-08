#!/bin/sh
# This will create a new PostgreSQL connect in Kafka Connect

curl -X "POST" "http://localhost:8082/connectors/" \
     -H "Content-Type: application/json" \
     -d '{
             "name": "heart-rate-connector-sink",
             "config": {
                 "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
                 "connection.url": "jdbc:postgresql://db:5432/heart_monitor?applicationName=heart-rate-connector",
                 "connection.user": "postgres",
                 "connection.password": "postgres",
                 "auto.create":"true",
                 "auto.evolve":"true",
                 "topics": "heart-rates",
                 "key.converter": "org.apache.kafka.connect.storage.StringConverter",
                 "transforms": "ExtractTimestamp",
                 "transforms.ExtractTimestamp.type": "org.apache.kafka.connect.transforms.InsertField$Value",
                 "transforms.ExtractTimestamp.timestamp.field" : "extract_ts"
             }
     }'
