package example

import org.apache.ignite.{IgniteQueue, Ignition}
import org.apache.ignite.configuration.CollectionConfiguration
import org.apache.ignite.services.ServiceConfiguration
import collection.JavaConverters._

object Run extends App {
  val ignite = Ignition.start("/home/ilya/Downloads/apache-ignite-fabric-2.1.0-bin/examples/config/example-ignite.xml")

  val cfg = new ServiceConfiguration()
  val cc = new CollectionConfiguration()

  val q:IgniteQueue[Integer] = ignite.queue("test", 1000, cc)
  cfg.setService(new ServiceFilter(q))
  cfg.setName("example_service")
  cfg.setTotalCount(2)

  val services = ignite.services()
  services.cancel("example_service")

  services.deploy(cfg)

  services.serviceDescriptors().forEach(service =>
    println(service.name(), service.totalCount())
  )

  while(true){
    println(q.take())
  }

}
