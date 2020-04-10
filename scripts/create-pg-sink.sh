#!/bin/sh
# This will create a new PostgreSQL connect in Kafka Connect

# Documentation on configuration:
# https://docs.confluent.io/current/connect/kafka-connect-jdbc/sink-connector/sink_config_options.html
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
                 "pk.mode": "kafka",
                 "topics": "heart-rates",
                 "key.converter": "org.apache.kafka.connect.storage.StringConverter",
                 "transforms": "ExtractTimestamp,RenameField",
                 "transforms.ExtractTimestamp.type": "org.apache.kafka.connect.transforms.InsertField$Value",
                 "transforms.ExtractTimestamp.timestamp.field" : "extract_ts",
                 "transforms.RenameField.type": "org.apache.kafka.connect.transforms.ReplaceField$Value",
                 "transforms.RenameField.renames" : "userId:user_id,isReset:is_reset"
             }
     }'
