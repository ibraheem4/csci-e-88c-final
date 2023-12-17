# CSCI-E-88C: Final Project

## Installation

Follow these steps to set up the environment for the project.

### Prerequisites

- Scala
- SBT (Scala Build Tool)
- Apache Spark
- Apache Kafka

### Steps

1. **Install Scala and SBT:**

    - Install Scala and SBT on your machine. You can download them from their respective official websites.

2. **Install Apache Spark:**

    - Use Homebrew to install Apache Spark:
      ```bash
      brew install apache-spark
      ```

3. **Install Apache Kafka:**

    - Kafka can be downloaded and installed manually from the official website. Here's a script to automate the process:

      ```bash
      # Download and install Apache Kafka
      install_kafka() {
        KAFKA_VERSION="3.6.1"
        KAFKA_DOWNLOAD_URL="https://downloads.apache.org/kafka/${KAFKA_VERSION}/kafka-${KAFKA_VERSION}-src.tgz"
        KAFKA_DIR="$HOME/kafka-${KAFKA_VERSION}"
        KAFKA_TGZ="kafka-${KAFKA_VERSION}-src.tgz"
 
        if [ -d "$KAFKA_DIR" ]; then
          echo "Apache Kafka ${KAFKA_VERSION} is already installed in ${KAFKA_DIR}."
        else
          echo "Downloading Apache Kafka ${KAFKA_VERSION}..."
          curl -L "${KAFKA_DOWNLOAD_URL}" -o "$KAFKA_TGZ"
          echo "Download completed. Extracting to ${KAFKA_DIR}..."
          mkdir -p "$KAFKA_DIR"
          tar -xzf "$KAFKA_TGZ" -C "$KAFKA_DIR" --strip-components=1
          echo "Apache Kafka ${KAFKA_VERSION} installed successfully in ${KAFKA_DIR}."
          echo "Removing downloaded file..."
          rm "$KAFKA_TGZ"
          echo "Downloaded file removed."
        fi
      }
 
      # Call function to install Kafka
      install_kafka
      ```

### Build Kafka

- After downloading Kafka, build it using the following commands:

  ```bash
  cd $HOME/kafka-3.6.1
  ./gradlew jar -PscalaVersion=2.13.11
  ```

### Start Zookeeper

- Kafka requires Zookeeper. Start it using:

  ```bash
  $HOME/kafka-3.6.1/bin/zookeeper-server-start.sh $HOME/kafka-3.6.1/config/zookeeper.properties
  ```

### Start Kafka Broker

- Start the Kafka broker with:

  ```bash
  $HOME/kafka-3.6.1/bin/kafka-server-start.sh $HOME/kafka-3.6.1/config/server.properties
  ```

## Usage

This project includes a Kafka producer (`CsvKafkaProducer`) that reads data from a CSV file and sends it to a Kafka topic, and a Spark Streaming application (`SalesDataProcessor`) that consumes this data, performs aggregations, and outputs the results.

### Input CSV File Format

The CSV file should have the following format:

```csv
Date,Product,Quantity,Price
2023-11-20 16:31:31,Beer,4,38.37
2023-12-06 16:31:31,Vodka,4,37.82
...
```

### Running the Producer

- Run `CsvKafkaProducer` to send data from the CSV to the Kafka topic `sales_data_topic`.

### Running the Spark Streaming Application

- Run `SalesDataProcessor` to consume data from the Kafka topic, aggregate total quantity and average price per product, and output the results.

### Example Output Table

The Spark application will output a table similar to the following:

```
+-----------+-------------+------------------+
| Product   | TotalQuantity | AveragePrice   |
+-----------+-------------+------------------+
| Appetizer | 225          | 32.93           |
| Gin       | 284          | 25.47           |
| ...       | ...          | ...             |
+-----------+-------------+------------------+
```

This table shows the total quantity sold and the average price for each product based on the data streamed from Kafka.
