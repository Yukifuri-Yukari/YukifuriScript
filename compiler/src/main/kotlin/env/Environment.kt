package yukifuri.script.compiler.env

data object Environment {
    val os: String = System.getProperty("os.name")
    val javaVersion: String = System.getProperty("java.version")
    val javaArchitecture: String = System.getProperty("os.arch")
    val javaVendor: String = System.getProperty("java.vendor")
    val jvm: String = System.getProperty("java.vm.name")
    val jvmVersion: String = System.getProperty("java.vm.version")
}
