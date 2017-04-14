#!/usr/bin/env bash

# 使用方法： sortAllFiles.sh lineNums ，其中lineNums表示按多少行数分割文件。
# 建议使用每个数据文件内的行数作为lineNums，可以用wc -l得到


if [ ! -n "$1" ]
then
    echo "$1"
    echo "Please give the line numbers of the small files as a parameter. Using 'sortAllFiles.sh lineNums.'"
    exit -1
fi

#1. 生成一个有序的大文件

for file in KNLIMIT*.data
do
{
  sort -n $file > $file.sorted
} &
done
wait
sort -smn *.sorted > merge.sorted




#2. 再切分成10个全局有序的小文件，方便JAVA处理。按照字节进行拆分

# 测试用的每个文件的大小作为参数传入
split -l $1 -a 1 merge.sorted sorted


# 处理完毕后删除中间结果
rm -f *.sorted

# 将结果移动到指定文件内
mv sorted* $(pwd)/tempFiles/