<div align="center">
  <h1>SCVM 文档</h1>
  <h3>SCVM Documents</h3>
</div>

# 指令 Commands

## 指令结构

push  114514 指令名称+参数(64位立即数)

## 对照表

### 名称 字节码 参数数量(立即数) 作用

### push

0x11 1

- 将立即数压入栈尾。

### pop

0x12 0

- 将栈尾立即数弹出。

### print

0x13 0

- 打印栈尾元素。

### add

0x14 0

- 弹出栈顶两个元素相加，结果压入栈。

### dup

0x15 0

- 复制栈顶元素，并再次压入栈。

### swap

0x16 0

- 交换栈顶两个元素。
