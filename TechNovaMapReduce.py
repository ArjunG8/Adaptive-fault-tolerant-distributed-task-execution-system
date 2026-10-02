import os
import csv
from datetime import datetime

from pyspark.sql import SparkSession
from pyspark.sql import functions as F


DATA_FILE = "technova_tasks.csv"

FIELDS = [
    "task_id",
    "task_name",
    "task_type",
    "workload",
    "node",
    "status",
    "created_at",
    "completed_at"
]

NODES = [
    ("NODE-01", "Arjun", 100),
    ("NODE-02", "Sarthak", 80),
    ("NODE-03", "Amar", 120),
    ("NODE-04", "Kaner", 70),
    ("NODE-05", "Jogi", 90)
]


def initialize_data():
    if not os.path.exists(DATA_FILE):
        with open(DATA_FILE, "w", newline="", encoding="utf-8") as file:
            writer = csv.DictWriter(file, fieldnames=FIELDS)
            writer.writeheader()


def read_records():
    initialize_data()

    with open(DATA_FILE, "r", newline="", encoding="utf-8") as file:
        return list(csv.DictReader(file))


def save_records(records):
    with open(DATA_FILE, "w", newline="", encoding="utf-8") as file:
        writer = csv.DictWriter(file, fieldnames=FIELDS)
        writer.writeheader()
        writer.writerows(records)


def task_exists(task_id):
    return any(
        record["task_id"] == task_id
        for record in read_records()
    )


def select_node(workload):
    records = read_records()

    current_load = {
        node_id: 0
        for node_id, _, _ in NODES
    }

    for record in records:
        if record["status"] in ("PENDING", "COMPLETED"):
            node = record["node"]

            if node in current_load:
                current_load[node] += int(record["workload"])

    available = []

    for node_id, node_name, capacity in NODES:
        load = current_load[node_id]

        if load + workload <= capacity:
            available.append(
                (node_id, node_name, capacity, load)
            )

    if not available:
        return None

    return min(
        available,
        key=lambda item: item[3]
    )


def submit_task():
    print()
    print("TASK SUBMISSION")
    print("-" * 55)

    task_id = input("Task ID       : ").strip()
    task_name = input("Task Name     : ").strip()
    task_type = input("Task Type     : ").strip().upper()

    try:
        workload = int(input("Workload      : ").strip())
    except ValueError:
        print("Invalid workload.")
        return

    if not task_id or not task_name or not task_type:
        print("All task details are required.")
        return

    if workload <= 0:
        print("Workload must be greater than zero.")
        return

    if task_exists(task_id):
        print("Task ID already exists.")
        return

    selected = select_node(workload)

    if selected is None:
        print("No node has enough capacity.")
        return

    node_id, node_name, capacity, current_load = selected

    remaining = capacity - current_load - workload

    record = {
        "task_id": task_id,
        "task_name": task_name,
        "task_type": task_type,
        "workload": workload,
        "node": node_id,
        "status": "PENDING",
        "created_at": datetime.now().strftime(
            "%Y-%m-%d %H:%M:%S"
        ),
        "completed_at": ""
    }

    records = read_records()
    records.append(record)
    save_records(records)

    print()
    print("TASK CREATED")
    print("-" * 55)
    print(f"Task ID       : {task_id}")
    print(f"Task          : {task_name}")
    print(f"Type          : {task_type}")
    print(f"Workload      : {workload}")
    print(f"Assigned Node : {node_name} ({node_id})")
    print(f"Remaining Cap.: {remaining}")
    print("Status        : PENDING")


def execute_tasks():
    records = read_records()

    pending = [
        record
        for record in records
        if record["status"] == "PENDING"
    ]

    if not pending:
        print()
        print("No pending tasks.")
        return

    print()
    print("TASK EXECUTION")
    print("-" * 70)

    for record in records:
        if record["status"] == "PENDING":
            record["status"] = "COMPLETED"
            record["completed_at"] = datetime.now().strftime(
                "%Y-%m-%d %H:%M:%S"
            )

            print(
                f"{record['task_id']} | "
                f"{record['task_name']} | "
                f"{record['node']} | COMPLETED"
            )

    save_records(records)

    print()
    print("Task execution completed.")


def show_records():
    records = read_records()

    print()
    print("TASK RECORDS")
    print("-" * 100)

    if not records:
        print("No task records available.")
        return

    print(
        f"{'ID':<10}"
        f"{'TASK':<32}"
        f"{'TYPE':<16}"
        f"{'LOAD':<8}"
        f"{'NODE':<12}"
        f"STATUS"
    )

    print("-" * 100)

    for record in records:
        print(
            f"{record['task_id']:<10}"
            f"{record['task_name'][:30]:<32}"
            f"{record['task_type']:<16}"
            f"{record['workload']:<8}"
            f"{record['node']:<12}"
            f"{record['status']}"
        )


def run_analytics():

    if not os.path.exists(DATA_FILE):
        print()
        print("No task data available.")
        return

    spark = (
        SparkSession.builder
        .appName("TECHNOVA-Task-Analytics")
        .master("local[2]")
        .config("spark.sql.shuffle.partitions", "2")
        .getOrCreate()
    )

    spark.sparkContext.setLogLevel("ERROR")

    print()
    print("=" * 60)
    print("TECHNOVA DISTRIBUTED TASK ANALYTICS")
    print("=" * 60)

    # Spark reads the task data directly.
    df = (
        spark.read
        .option("header", "true")
        .option("inferSchema", "true")
        .csv(DATA_FILE)
    )

    print()
    print("MAP PHASE")
    print("-" * 60)

    mapped = df.select(
        "node",
        "task_id",
        "workload"
    )

    mapped.show(truncate=False)

    print()
    print("REDUCE PHASE")
    print("-" * 60)

    reduced = (
        mapped
        .groupBy("node")
        .agg(
            F.count("task_id").alias("task_count"),
            F.sum("workload").alias("total_workload")
        )
        .orderBy("node")
    )

    reduced.show(truncate=False)

    print()
    print("NODE ANALYTICS")
    print("-" * 60)

    (
        df.groupBy("node")
        .agg(
            F.count("*").alias("tasks"),
            F.sum("workload").alias("total_workload"),
            F.round(
                F.avg("workload"), 2
            ).alias("average_workload")
        )
        .orderBy("node")
        .show(truncate=False)
    )

    print()
    print("TASK TYPE ANALYTICS")
    print("-" * 60)

    (
        df.groupBy("task_type")
        .count()
        .orderBy("task_type")
        .show(truncate=False)
    )

    print()
    print("STATUS ANALYTICS")
    print("-" * 60)

    (
        df.groupBy("status")
        .count()
        .orderBy("status")
        .show(truncate=False)
    )

    print()
    print("SUMMARY")
    print("-" * 60)

    summary = df.agg(
        F.count("*").alias("total_tasks"),
        F.sum(
            F.when(
                F.col("status") == "COMPLETED",
                1
            ).otherwise(0)
        ).alias("completed"),
        F.sum(
            F.when(
                F.col("status") == "PENDING",
                1
            ).otherwise(0)
        ).alias("pending"),
        F.sum("workload").alias("total_workload")
    )

    summary.show(truncate=False)

    print("Analytics completed.")

    spark.stop()

def main():

    initialize_data()

    print()
    print("=" * 60)
    print("TECHNOVA DISTRIBUTED TASK SYSTEM")
    print("DISTRIBUTED TASK ANALYTICS")
    print("=" * 60)

    while True:

        print()
        print("submit  - create a task")
        print("execute - execute pending tasks")
        print("records - view task records")
        print("analyze - run distributed analytics")
        print("exit    - close")

        command = input("> ").strip().lower()

        if command == "submit":
            submit_task()

        elif command == "execute":
            execute_tasks()

        elif command == "records":
            show_records()

        elif command == "analyze":
            run_analytics()

        elif command == "exit":
            print("System closed.")
            break

        else:
            print("Invalid command.")


if __name__ == "__main__":
    main()