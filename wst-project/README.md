>写在前面: 
> 1. 赛题答疑联系人（可通过钉钉联系）：万少
> 2. 在开始coding前请仔细阅读以下内容
> 3. 内部赛钉钉群：11751103


# 1. 题目背景
题目主要解决的是NewSQL领域中使用最频繁的一个场景:分页排序，其对应的SQL执行为order by id limit k,n
主要的技术挑战为"分布式"的策略，赛题中使用多个文件模拟多个数据分片。

# 2. 题目描述

## 2.1 题目内容
给定一批数据，求解按顺序从小到大，顺序排名从第k下标序号之后连续的n个数据 

例如：
top(k,3)代表获取排名序号为k+1,k+2,k+3的内容,例:top(10,3)代表序号为11、12、13的内容,top(0,3)代表序号为1、2、3的内容
需要考虑k值几种情况，k值比较小或者特别大的情况，比如k=1,000,000,000
对应k,n取值范围： 0 <= k < 2^63 和 0 < n < 100


## 2.2 语言限定
限定使用JAVA语言




# 3.  程序目标

你的coding目标是实现一个继承自KNLimit接口的KNLimitImpl类，对其中的processTopKN(long k,int n)方法做实现，解决top(k,n)问题。
在结果正确的基础上，处理时间越短越好。




# 4. 参赛方法说明

用户从阿里云git仓库下载内部赛的接口和示例工程，并且实现指定接口。

阿里云内部赛git仓库地址： https://code.aliyun.com/middlewarerace2017/LimitKNDemo/tree/master

1. 选手定义主类的类名为"TopKN"的类，实现KNLimit接口并且实现自己的processTopKN(long k,int n)方法
2. TopKN类中的main方法调用选手实现的processTopKN方法。方法中的k,n的值通过main方法中的args参数按顺序获取。
2. 选手实现的processTopKN方法从指定的文件目录读取数据文件（需要符合命名规则），进行处理
3. 将自己处理的结果文件，按照命名规则输出到指定目录


PS：
1. 请确保自己的实现主类的类名为"TopKN"，我们的校验排名程序在验证选手代码的时候会去调用TopKN的main方法。为了保证您参赛成绩的有效性，
请确保自己实现类的类名命名准确。
2. 请确保k,n参数的值从main方法的args中按顺序获取

# 5. 数据文件说明

1. 文件个数：10
2. 每个文件大小：1G
3. 文件内容：由纯数字组成，每一行的数字代表一条数据记录
4. 每一行数字的大小取值范围 0 <= k < 2^63 （数字在Long值范围内均匀分布）
5. 数据文件的命名严格按照规则命名。命名规则："KNLIMIT_X.data" ，其中X的范围是[0,9]
6. 数据文件存放目录为/home/admin/topkn-datafiles/


PS: 服务端的数据文件均放在指定目录下，按照指定的命名规则命名。请确保提交的代码从指定路径下以正确的文件名读取文件。否则，将导致算法的校验逻辑失败。


# 6. 结果文件说明
1. 结果文件命名规则：RESULT.rs  ，比赛进行五轮测试，每轮测试都会生成新的RESULT.rs
2. 结果文件输出目录：/home/admin/topkn-resultfiles/teamCode


PS: 
1. 结果文件的命名和输出必须严格符合赛题要求，否则会影响程序的校验和排名。以上teamCode目录是选手参赛时的teamCode，每个人不同，请留意。
2. **结果文件中的每一行记录后面都必须跟一个回车符，因为最后比对结果用的是MD5，你少个回车符可能比对也会失败，切记！**

# 7. 测试环境描述
测试环境为相同的4核虚拟机，内存为4GB，磁盘使用不做限制(可用空间为200G)。给定的JVM堆大小为2.5G。

PS:
1. 整个机器可使用的内存为4057084kB。选手的代码执行时，JAVA_OPTS="-Xms2500m -Xmx2500m -XX:+UseConcMarkSweepGC -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails "
2. 不准使用堆外内存。



# 8. 程序校验逻辑
1. 参赛选手给出git地址
2. 结果校验和排名程序每天会从提交的git地址上拉取代码进行结果验证和排名
3. 校验程序会给出随机的k值和n值，作为processTopKN(long k,int n)的输入。其中k的取值范围是[0,TOTAL_LINE_NUM-100]， n的取值范围是(0,100]。其中TOTAL_LINE_NUM表示所有数据文件的总行数。参赛程序所需的数据文件
在结果校验程序所在服务器会随机生成。
4. 校验程序调用选手提供的TopKN中的主方法，生成一轮测试的结果文件RESULT.rs
5. 用户处理得到的结果文件会和校验服务端的"标准答案"文件一一做MD5校验，如果全部一致，则认为算法正确，计算耗时并且统计排名。
6. 重复执行第3至第5步，完成总共5轮测试（5轮测试使用的10个数据文件是相同的）。如果5轮测试的结果文件全部正确，则累计5轮处理的耗时，统计排名




PS:
1. 结果校验时会按顺序校验5份结果文件。单次时间计算，会从传入top的k和n参数作为时间开始,在得到所有n条结果数据后结束时间计算，两个时间差值为单次耗时。
整个top(k,n)的接口调用会在结果校验通过后,串行执行5次(每次k,n参数会不同,k的值会相对比较分散),把5次的单次耗时累加即为总耗时。
2. 调用选手的程序是通过脚本"java -cp $yourJarPath com.alibaba.middleware.race.TopKN $k $n" 这样的形式来调用的。该脚本会调用5次，当然每次的k,n都是随机的。


# 9. 排名规则

在结果校验100%正确的前提下，按照总耗时进行排名，耗时越少排名越靠前，时间精确到毫秒



# 10. 第二/三方库规约

* 公平起见，仅允许依赖JavaSE 7 包含的lib
* 可以将开源三方库以拷贝的方式拷贝到自己的项目中，但在review阶段会有所减分


# 11. 示例工程说明

在[内部赛接口示例工程](https://code.aliyun.com/middlewarerace2017/LimitKNDemo/tree/master)中有个Demo类。该
类结合sortAllFiles.sh这个shell脚本来完成了topKN问题的解决。实现上未考虑性能优化，仅仅做演示使用。

示例工程实现思路：
1. 通过sortAllFiles.sh将10个文件内有序的小文件处理成为10个全局有序的小文件
2. 通过demo目录下中Demo类里面的方法，按序读取10个全局有序的小文件，得到top(k,n)的结果


# 12. 关于选手中间结果的补充

1. 比赛过程中，选手可以自由利用磁盘的空间。中间结果在进行选手的5轮测试的时候，均可以使用，不会被清空。选手的5轮测试结束后，选手的中间结果会被清空，下次评测使用中间结果需要重新生成。程序校验结束后，所有中间结果全部清空。中间结果请输出到指定目录：/home/admin/middle/teamCode下。

2. 请不要投机取巧将中间结果写入到非指定目录，然后在下一次再校验的时候去读取，从而来改进自己的比赛成绩。校验程序不会特地去清理写在其他目录选手产生的临时、中间结果文件，但是工作人员会不定时去服务器上抽查。

3. 请大家诚信为本，靠自己真正的实力赢得比赛。比赛后续还有答辩，所以投机取巧是不可行哦。


# 13. 代码提交相关的补充

代码提交评测的步骤：
1. 选手将自己的代码提交到[阿里云code](https://code.aliyun.com)上，并且在自己的私人项目添加项目成员"middlewarerace2017"，并将其角色设置为：develeoper
2. 选手在天池系统上通过设置自己的代码git仓库地址（此时会生成teamCode，该teamCode会在很多地方用到，例如写指定的中间文件目录、结果文件目录等）
3. 选手点击提交按钮，评测机器会自己拉取选手的代码，build选手的代码，然后运行相应的jar。
4. 如果选手代码的结果是正确的，则消耗掉一次评测机会，每天有2次评测机会。如果选手代码出现异常终止，不消耗评测次数。

PS：
1.  建议选手本地先运行"mvn clean assembly:assembly -Dmaven.test.skip=true" 命令，确保能正确构建再提交
2.  不要忘记添加指定成员为develeoper

> 此外，在正式开放代码提交入口之前，已经完成代码编写的同学，可以钉钉联系我参与评测系统的内测，完善评测系统。测试的结果不计入正式比赛排名。


# 14. FAQ补充

#### 1. 请大家在向天池系统提交代码前，仔细检查自己的代码，避免由于程序问题影响天池的校验程序。一些恶意行为的话，可能会被追究责任哟。例如在非指定目录写入大量文件把天池评测系统磁盘撑爆、编写恶意代码破坏天池系统正常评测等等。

#### 2. 是否可以使用堆外内存？答：不可以，仅在堆内内存处理数据。例如MappedByteBuffer、DirectByteBuffer这些涉及堆外内存的类不可以使用。

#### 3. 是否可以使用JAVA以外的语言？ 答：不可以，JNI调用、shell脚本调用都不行

#### 4. 最大文件打开数是多少？

```
$ulimit -a
core file size          (blocks, -c) 0
data seg size           (kbytes, -d) unlimited
scheduling priority             (-e) 0
file size               (blocks, -f) unlimited
pending signals                 (-i) 31546
max locked memory       (kbytes, -l) 64
max memory size         (kbytes, -m) unlimited
open files                      (-n) 65535
pipe size            (512 bytes, -p) 8
POSIX message queues     (bytes, -q) 819200
real-time priority              (-r) 0
stack size              (kbytes, -s) 10240
cpu time               (seconds, -t) unlimited
max user processes              (-u) 31546
virtual memory          (kbytes, -v) unlimited
file locks                      (-x) unlimited
```

#### 5. CPU信息怎样的？答：4核，其中1个核的信息如下：

```
processor	: 3
vendor_id	: GenuineIntel
cpu family	: 6
model		: 45
model name	: Intel(R) Xeon(R) CPU E5-2430 0 @ 2.20GHz
stepping	: 7
cpu MHz		: 2199.996
cache size	: 15360 KB
physical id	: 0
siblings	: 4
core id		: 3
cpu cores	: 4
apicid		: 3
initial apicid	: 3
fpu		: yes
fpu_exception	: yes
cpuid level	: 13
wp		: yes
flags		: fpu vme de pse tsc msr pae mce cx8 apic mtrr pge mca cmov pat pse36 clflush mmx fxsr sse sse2 ss ht syscall nx pdpe1gb rdtscp lm constant_tsc rep_good unfair_spinlock pni pclmulqdq ssse3 cx16 sse4_1 sse4_2 x2apic popcnt aes xsave avx hypervisor lahf_lm xsaveopt
bogomips	: 4399.99
clflush size	: 64
cache_alignment	: 64
address sizes	: 46 bits physical, 48 bits virtual
power management:
```

#### 6. 磁盘信息怎样的？

```
Disk /dev/vda: 268.4 GB, 268435456000 bytes
255 heads, 63 sectors/track, 32635 cylinders
Units = cylinders of 16065 * 512 = 8225280 bytes
Sector size (logical/physical): 512 bytes / 512 bytes
I/O size (minimum/optimal): 512 bytes / 512 bytes
Disk identifier: 0x0002d39e

   Device Boot      Start         End      Blocks   Id  System
/dev/vda1   *           1       32636   262142976   83  Linux
```

#### 7. k值超过总行数的情况要考虑吗？答: 不需要考虑，k+n的值肯定是小于等于总行数的

#### 8. 生成的数据是否会重复，是否需要去重？答：不需要去重，生成数据的方法可以参考样例工程中的数据生成方法

#### 9. JDK版本详情：

```
java version "1.7.0_80"
Java(TM) SE Runtime Environment (build 1.7.0_80-b15)
Java HotSpot(TM) 64-Bit Server VM (build 24.80-b11, mixed mode)
```

#### 10. 可以使用多线程吗？答：可以

#### 11. 可以用java的Runtime.execute和ProcessBuilder.start等方法来启动LINUX的守护进程吗？答：不可以


#### 12. 可以使用nio吗？答：不可以。之前已经提到过不准使用堆外内存，由于nio的实现大量依赖堆外内存，我们这里也禁止使用。禁止使用意味着java.nio包下面的所有类不可以用，以及依赖java.nio的类的所有实现都不可以用。

#### 13. 生成的数据会重复吗？答：有可能会重复，但是不需要做去重。

#### 14. 是否开启了超线程？答：已经开启

#### 15. 磁盘读写速度怎样？

```
# 执行简单测试结果如下
$sudo  time dd if=/dev/zero of=/var/test bs=8k count=1000000
1000000+0 records in
1000000+0 records out
8192000000 bytes (8.2 GB) copied, 182.591 s, 44.9 MB/s
0.26user 23.97system 3:02.59elapsed 13%CPU (0avgtext+0avgdata 3776maxresident)k
112inputs+16000000outputs (0major+363minor)pagefaults 0swaps
```