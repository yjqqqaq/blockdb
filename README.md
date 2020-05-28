# blockdb
这是一个小组的project 3, 小组成员名单如下：

计科70 杨景钦 2017011314

计科70 高杰 2017011383

计科70 陈柯润 2017012509



### 如何编译这个项目

git clone到本地后，执行compile.sh即可。 执行完成后，target文件夹下应当有blockdb-1.0-SNAPSHOT.jar



### 如何运行这个项目

在项目根目录下，打开命令行，输入`java -jar ./target/blockdb-1.0-SNAPSHOT.jar` 即可启动service，如果需要clean start，请输入`java -jar ./target/blockdb-1.0-SNAPSHOT.jar clean。`

当然，你也可以直接运行`start.sh`（clean start 是`startClean.sh`）。但需要注意的是，server启动的时候，需要保证根目录下存在`/tmp`文件夹，如果不存在，请自行创建（当然，该git项目下是存在的）

运行client端，则需要指定jar中的main class，利用命令`java -cp ./target/blockdb-1.0-SNAPSHOT.jar iiis.systems.os.blockdb.BlockDatabaseClient`

需要注意的是，如果想与server进行通信，加上你想执行的命令即可。 例如，`java -cp ./target/blockdb-1.0-SNAPSHOT.jar iiis.systems.os.blockdb.BlockDatabaseClient get test0000 `  表示获取userID为`test0000`的用户的值。 具体的格式可以参考`test.sh`中的运行方式。



### 自测数据

我们在`/test`文件夹下准备了自测脚本`test.sh`，不过其运行需要在项目的根目录，于是我们在根目录下也提供了一份`test.sh` （两者完全一致）。自测脚本测试的内容以`echo`的方式输出了出来。

`test.sh` 会自动生成`/tmp`文件夹，并且会clean start服务，以便多次测试运行。



### 写在最后

本次项目完全在windows环境下开发（linux下只进行了测试）。如果在编译/运行/测试的时候遇到困难，请联系我们。