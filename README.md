# Minibase Project

## Overview

Minibase is a Java-based project developed for the Advanced Database Systems course. It implements a lightweight database engine focused on the minimization procedure for conjunctive queries, evaluation of these queries.

## Installation

Ensure you have Java 8 or later installed. This project uses Maven for dependency management and building. To set up the project, clone the repository and navigate to the project directory.

```bash
git clone [repository_url]
cd minibase
```

Compile the project using Maven:

```bash
mvn clean compile assembly:single
```

## Usage

### Task 1: CQ Minimization

To run the CQMinimizer:

```bash
java -cp target/minibase-1.0.0-jar-with-dependencies.jar ed.inf.adbs.minibase.CQMinimizer [input_file] [output_file]
```

### Task 2: Query Evaluation

To evaluate queries using Minibase:

```bash
java -cp target/minibase-1.0.0-jar-with-dependencies.jar ed.inf.adbs.minibase.Minibase [db_directory] [query_file] [output_file]
```

## License

This project is licensed under the terms of the MIT license.
