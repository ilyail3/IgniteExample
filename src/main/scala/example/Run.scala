package example

import java.util
import java.util.Map

import org.apache.ignite.lang.{IgnitePredicate, IgniteRunnable}
import org.apache.ignite.stream.StreamReceiver
import org.apache.ignite._
import org.apache.ignite.cache.CachePeekMode
import org.apache.ignite.cluster.ClusterNode
import org.apache.ignite.compute.{ComputeJobResult, ComputeTaskAdapter}
import org.apache.ignite.services.{Service, ServiceConfiguration, ServiceContext}

import collection.JavaConverters._

class WaitRunnable(latch: IgniteCountDownLatch) extends IgniteRunnable{
  override def run() = {
    Thread.sleep(10000)
    latch.countDown(1)
  }
}

class StreamReceiverWorker(counter:IgniteAtomicLong) extends StreamReceiver[Integer, String]{
  override def receive(cache: IgniteCache[Integer, String], entries: util.Collection[Map.Entry[Integer, String]]) = {
    entries.forEach(entry => {
      // println("receiver ", entry.getValue, cache.size(CachePeekMode.ALL))
      counter.incrementAndGet()
    })
  }
}

class TaskFactory extends ComputeTaskAdapter[Integer,String] {
  override def reduce(results: util.List[ComputeJobResult]) = {
    println(results)
    ""
  }

  override def map(subgrid: util.List[ClusterNode], arg: Integer) = {
    println(subgrid)
    println(arg)

    null
  }
}

class ServiceFilter(q:IgniteQueue[Integer]) extends Service{
  var counter:Integer = 0

  override def cancel(ctx: ServiceContext) = {
    println("service cancel")
  }

  override def init(ctx: ServiceContext) = {
    println("service init")

    counter = 0
  }

  override def execute(ctx: ServiceContext) = {
    while(true){
      Thread.sleep(1000)
      q.put(counter)

      counter += 1
    }
  }
}

