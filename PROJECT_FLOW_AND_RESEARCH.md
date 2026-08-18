# Adaptive Fault-Tolerant Distributed Task Execution Platform

## 1. What is this project?

We are building a small distributed-computing backend in which multiple independent node processes cooperate to execute computational tasks.

The complete system will eventually contain:

Client -> Coordinator/Leader -> Scheduler -> Worker Nodes -> Result

The project will run initially on a **single PC**. Each logical node will be a separate Java process using a different port. For example:

- Node 1 -> localhost:8001
- Node 2 -> localhost:8002
- Node 3 -> localhost:8003

The physical machine is one PC, but the software processes behave as independent distributed nodes and communicate through RMI/network interfaces.

## 2. Why are we building it?

The purpose is to map the Distributed Computing laboratory experiments into one coherent backend rather than implementing every experiment as an unrelated program.

The final platform should be able to:

1. Accept computational tasks from a client.
2. Communicate with worker nodes using RPC/RMI.
3. Execute multiple tasks using multithreading.
4. Monitor worker health.
5. Distribute tasks according to worker load.
6. Detect node/leader failure.
7. Elect a new leader using Bully/Ring election.
8. Maintain replicated task state.
9. Use logical/physical clock synchronization where required by the lab.
10. Recover pending work after failures.
11. Execute parallel/distributed computation tasks such as MapReduce/MPI-style workloads.

## 3. Today's implementation

Today's version intentionally implements only the first layer:

Client -> Java RMI -> Worker -> Task execution -> Result

We use the same RMI structure taught in the laboratory:

- Remote interface
- Remote implementation
- RMI server/registry
- RMI client
- Remote method invocation

The current remote method is:

`executeTask(taskType, input)`

Supported demo tasks:

- WORD_COUNT
- UPPERCASE

Example:

Client sends:

`WORD_COUNT`
`distributed computing is interesting`

Worker returns:

`Word Count = 4`

This is the foundation for the future task-execution service.

## 4. How today's code maps to the final project

### TaskService.java

Remote interface. Later this becomes part of the node-to-node communication layer.

### TaskServiceImpl.java

Worker-side task execution. Later this will contain the actual worker/task-execution logic.

### TaskServer.java

Starts the worker's RMI registry and publishes the remote service.

### TaskClient.java

Represents a client submitting a computational task.

## 5. Planned evolution

### Phase 1 - RMI task execution
Current phase.

Client -> Worker -> Result

### Phase 2 - Multiple workers

Client -> Coordinator -> Worker 1
                         -> Worker 2
                         -> Worker 3

### Phase 3 - Multithreading

Each worker gets a thread pool and can execute several tasks concurrently.

### Phase 4 - Load balancing

The coordinator measures:

- CPU utilization
- Active task count
- Queue length
- Response time

and selects an appropriate worker.

### Phase 5 - Failure detection

Nodes exchange heartbeats.

If a node stops responding, it is marked failed.

### Phase 6 - Leader election

If the coordinator/leader fails:

- Bully election
- Ring election

will be implemented and compared.

### Phase 7 - Replication

Important task metadata will be replicated so that a newly elected leader can recover pending tasks.

### Phase 8 - Clock synchronization

Implement the clock-synchronization experiment required by the laboratory, including logical ordering where appropriate.

### Phase 9 - Parallel/distributed computation

Add computational tasks that can use MapReduce/MPI-style parallel execution.

### Phase 10 - Evaluation

Compare:

- Round Robin vs adaptive scheduling
- Bully vs Ring election
- With vs without replication
- Normal execution vs failure recovery

Important metrics:

- Task completion time
- Response time
- Throughput
- CPU utilization
- Election time
- Recovery time
- Number of messages
- Task loss/recovery rate

## 6. Experiment-to-project mapping

| DC experiment | Final project module |
|---|---|
| RPC / Java RMI | Client-to-node and node-to-node task communication |
| Multithreading | Worker thread pool |
| Clock synchronization | Logical/physical clock module |
| Bully election | Leader failover |
| Ring election | Alternative leader failover |
| Mutual exclusion | Controlled access to shared task/state resources, if required by the lab |
| Data replication | Replicated task metadata/state |
| Load balancing | Adaptive task scheduler |
| MapReduce | Parallel data-processing task |
| MPI / distributed communication | Parallel worker communication |
| Fault tolerance | Heartbeats, failure detection and task recovery |

## 7. Research direction

The research direction is NOT to claim that each individual mechanism is new.

Instead, the preliminary literature review suggests that IEEE research commonly focuses on particular aspects such as task scheduling, load balancing, leader election, fault-tolerant scheduling, or replication.

Our project will investigate the integration of these mechanisms into one lightweight, experimentally controllable distributed task-execution backend.

The exact novelty claim must be refined after a deeper full-text literature review.

## 8. Preliminary research gap from IEEE papers

### Paper 1
**Adaptive Scheduling Algorithm Based Task Loading in Cloud Data Centers**

IEEE Xplore:
https://ieeexplore.ieee.org/document/9759277/

Focus:
Adaptive scheduling based on task loading.

Preliminary gap relevant to our project:
Scheduling is the main focus; the abstract/title do not indicate an integrated leader-election, replication, and failure-recovery mechanism.

### Paper 2
**Optimizing Cloud Computing Performance With an Enhanced Dynamic Load Balancing Algorithm for Superior Task Allocation**

IEEE Xplore:
https://ieeexplore.ieee.org/document/10771720

Focus:
Dynamic load balancing and task allocation.

Preliminary gap:
The work is centered on load-balancing performance rather than an end-to-end node coordination framework combining election, replication, synchronization, and recovery.

### Paper 3
**Distributed Task Scheduling in Serverless Edge Computing Networks for the Internet of Things: A Learning Approach**

IEEE Xplore:
https://ieeexplore.ieee.org/document/9757233

Focus:
Learning-based distributed task scheduling.

Preliminary gap:
The emphasis is on scheduling decisions; our project will investigate a simpler, interpretable scheduler combined with failure handling and leader coordination.

### Paper 4
**Distributed Task Scheduling in Heterogeneous Fog Networks: A Matching with Externalities Method**

IEEE Xplore:
https://ieeexplore.ieee.org/document/9049775/

Focus:
Distributed task scheduling in heterogeneous fog environments.

Preliminary gap:
The scheduling problem is treated as the primary optimization problem; integrated leader election, task-state replication and recovery are outside the core scope indicated by the title/abstract.

### Paper 5
**Towards an Optimized Heterogeneous Distributed Task Scheduler in OpenMP Cluster**

IEEE Xplore:
https://ieeexplore.ieee.org/abstract/document/10820656/

Focus:
Distributed task scheduling, HEFT and Work Stealing in OpenMP clusters.

Preliminary gap:
The work improves scheduling mechanisms, but the abstract emphasizes scheduling/runtime optimization rather than a complete fault-tolerant control plane with leader election and replicated task state.

### Paper 6
**Self-Healing Redundancy for OpenStack Applications through Fault-Tolerant Multi-Agent Task Scheduling**

IEEE Xplore:
https://ieeexplore.ieee.org/document/7830740

Focus:
Fault tolerance, redundancy and multi-agent task scheduling.

Preliminary gap:
This is particularly close to our direction. Our project can focus on a smaller educational distributed cluster and explicitly integrate classical DC laboratory algorithms such as Bully/Ring election and clock synchronization with task scheduling.

### Paper 7
**Data Replication for Reducing Computing Time in Distributed Systems with Stragglers**

IEEE Xplore:
https://ieeexplore.ieee.org/document/9006012

Focus:
Replication to reduce computing time caused by stragglers.

Preliminary gap:
Replication is studied as a performance technique. Our project will use replication for both task-state availability/recovery and evaluate its interaction with leader failover and scheduling.

### Paper 8
**3-Phase Leader Election Algorithm for Distributed Systems**

IEEE Xplore:
https://ieeexplore.ieee.org/document/8819837

Focus:
Leader election.

Preliminary gap:
Leader election is treated as the central problem. The project will connect leader election with task ownership, scheduling, replicated state and post-failure task recovery.

## 9. Overall preliminary gap

From the titles and accessible abstracts, the recurring pattern is:

- Scheduling papers optimize scheduling.
- Load-balancing papers optimize load distribution.
- Leader-election papers optimize election.
- Replication papers optimize replication/straggler handling.
- Fault-tolerance papers focus on resilient execution.

This gives us a research direction:

> **Develop and evaluate a lightweight distributed task-execution backend that coordinates adaptive task scheduling, leader election, replicated task state, and failure recovery as interconnected mechanisms rather than treating them as isolated algorithms.**

Important: this is a **preliminary research gap**, not a claim that no IEEE paper has ever combined these mechanisms. Before writing a publication-quality novelty claim, we should perform a broader IEEE Xplore full-text review.

## 10. Proposed problem statement

> Existing distributed computing solutions frequently optimize individual concerns such as task scheduling, load balancing, leader election, replication, or fault tolerance. This project proposes a lightweight distributed task-execution backend that integrates these mechanisms so that computational tasks can be dynamically assigned, node failures can be detected and recovered from, leadership can be transferred, and task state can remain available during failures. The system will be implemented as multiple independent node processes on a single host and evaluated using task completion time, response time, throughput, communication overhead, election time, and recovery time.

## 11. Final architecture

```text
                         CLIENT
                           |
                           | RMI
                           v
                    +-------------+
                    |   LEADER    |
                    | Coordinator |
                    +------+------+
                           |
                    Adaptive Scheduler
                    /        |                           v         v         v
                Node 2     Node 3    Node 4
                Worker     Worker    Worker
                   |         |         |
                Threads   Threads   Threads
                   \         |         /
                    \        |        /
                     Task Results

       +----------------------------------------+
       | Distributed Coordination                |
       |                                        |
       | Heartbeat / Failure Detection           |
       | Bully Election                          |
       | Ring Election                           |
       | Clock Synchronization                   |
       | Task-State Replication                  |
       | Fault Recovery                          |
       +----------------------------------------+
```

## 12. First demo flow

Run the worker:

`TaskServer`

Then run:

`TaskClient`

Expected flow:

```text
TaskClient
    |
    | lookup("TaskService")
    |
    | executeTask("WORD_COUNT", input)
    v
TaskServer / Worker
    |
    | process task
    v
"Word Count = 4"
    |
    v
TaskClient
```

This first demo is deliberately simple. It is the RMI communication layer that will later become the communication foundation of the full distributed system.

## 13. Important implementation principle

Do not replace this first implementation with Spring Boot/gRPC yet.

Your instructor taught Java RMI, so the first milestone deliberately follows the same logic. Once this works, we will extend it into multiple independent nodes and then add the other DC experiments.
