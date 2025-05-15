# Intellij快捷键(MAC)
| Command | MAC | Windows |
| :---   | :---- |    ---: |
| **Copy Reference 复制包名路径** | `shift⇧ + option ⌥ + command ⌘ + C` |  |
| **Paste as Plain Text 粘贴包名路径** | `shift⇧ + option ⌥ + command ⌘ + V` |  |
|Show emoji list| `fn + E` | NONE|
|←Back | `option ⌥ + command ⌘ + ←` | `Ctrl + Alt + ←` |
|→Forward | `option ⌥ + command ⌘ + →` | `Ctrl + Alt + →`|
|basic 提示 | `control(⌃)  + space` | `Ctrl + Space` |
|注释代码块 | `shift⇧ + control ⌃ + /` | `Shift + Ctrl + /` |
|expand collapse code | `command ⌘ + + -` | `Ctrl + +/-`|
|fast find | ` shift⇧ + shift⇧  ` | `Shift + Shift` |
|Find | `command ⌘ + F` | `Shift + F` |
|Fast Replace| `command ⌘ + R` | `Shift + R` |
|recent file | ` command ⌘ + E ` | `Shift + E` |
|UpperCase LowerCase | ` shift⇧ + command ⌘ + U ` | `Ctrl + Shift + U`|
|**compile** | `command ⌘ + F9` | `Ctrl + F9/B` |
|**complete... 结束代码行** | `command ⌘ + shift⇧ + Entry` | `Ctrl + Shift + Enter` |
|🦋**异常捕获 Surround With**🦋 | `option ⌥ + command ⌘ + T` | `Ctrl + Alt + T` |
|🦋**Parameter Info**🦋 | `command ⌘ + P` | `Ctrl + P` |
|🦋**重构 Refactor**🦋 | `control^ + T` | `Ctrl + Alt + Shift + T` |
|**小灯泡 Show Intention Actions** | `option ⌥ + ⏎` | `Alt + Enter`|
|**自动应用变量 Introduce Variable** | `option ⌥ + command ⌘ + V` | `Ctrl + Alt + V`|
|🦋**实现类 Implementation(s)**🦋 | `option ⌥ + command ⌘ + B` | `Ctrl + Alt + B`|
|🦋**去接口 Go to Super Method**🦋 | `command ⌘ + U` | |
|🦋**Generate**🦋 | `command ⌘ + N` | `Alt + Insert` |
|**Move Caret to Code Block End** |`option ⌥ + command ⌘ + ]`| `Ctrl + ]` |
|**Move Caret to Code Block Start** |`option ⌥ + command ⌘ + [`| `Ctrl + [`|
|Add Rectangular Selection on Mouse Drag 鼠标多行选中 |`control^ + option ⌥ + command ⌘ + button1`|`Shift + Ctrl + Alt + button1`|
|Find Next / Move to Next Occurrence |`command ⌘ + G`||
|Find Previous / Move to Previous Occurrence |`command ⌘ + shift⇧ + G`||
|🦋**同时选中所有匹配 Select All Occurrences**🦋 |`control^ + command ⌘ + G`|`Shift + Ctrl + Alt + J`|
|🦋**选中匹配 Add Selection for Next Occurrence**🦋 |`control^ + G`|`Alt + J`|
|🦋**撤回选中匹配 Unselect Occurrence**🦋 |`control⌃ + shift⇧ + G`|`Shift + Alt + J`|
|**Find in Path...** |`command ⌘  + shift⇧ + F`||
|redo | `shift⇧ + command ⌘ + Z` | `Ctrl + Shift + Z`|
|undo | `command ⌘ + Z` | `Ctrl + Z`|
|move statement up down代码移动 | `shift⇧ + command ⌘ + ↑ or ↓` | `Ctrl + Shift + up or down`|
|duplicate entire lines复制代码 | `command ⌘ + D` | `Ctrl + D`|
|Move Caret Word | `option ⌥ + ←/→` | `Ctrl + ←/→` |
|go to line**跳转到行** | `command ⌘ + L` | `Ctrl + G` |
|翻译 | `control ⌃ + command ⌘ + U` ||
|删除行 | `command(⌘)+ del ⌦` | `Ctrl + Y`|
|main 方法 | ` psvm `||
|输出 | ` sout `||

```python
def hello():
    return 'Hello World'
```

## IDEA Plugins Recommend

- **CamelCase**
- **GsonFormatPlus**
- **Json Helper**(去格式化/格式化json)
- **Mapstruct Support**
- **Maven Helper**
- **POJO to Json**(对象转json)
- **~~RestfulTool~~**(最新版已经内置 ⇧+⌘+\\)
- **Sonarlint**
- **VisualVM Launcher**
- Json Parser(optional)
- **JPA Buddy**(Ultimate 专属，可以快速生成getter interface)
- **JSON Schema Generator** (https://plugins.jetbrains.com/plugin/22597-json-schema-generator)
- plantuml4idea ([一个画时序图的插件](https://plantuml.com/zh-dark/sequence-diagram#0b2e57c3d4eafdda))


idea gradle 在控制台输出中文乱码解决方式
help->Edit Custom VM Options 追加 `-Dfile.encoding=UTF-8`

激活
https://gitee.com/bluelovers/jetbrains-agent

## 关于利用全局搜索正则表达式实现快速替换的方法

实现全局搜索，并且替换的例子

```yaml
# 搜索并且删除 protocol: https
my-cluster:
	protocol: https
-->	
my-cluster:

#使用正则表达式
(?<name>.*-cluster:)
  protocol: https
 
#将表达式的内容给予组名 name --> 
#使用$1表示第一个组名，并且替换
$1

lb-cluster:
  - instanceId: (?<clu1>.*)
    host: (?<host1>.*)
    port: (?<port1>.*)
    secure: (?<sec1>.*)


lb-cluster:
  - instanceId: ${clu1}
    host: ${host1}
    port: ${port1}
    secure: ${sec1}        
```







# VSCode快捷键(MAC)



| Command                                                      | MAC                                    | Windows          |
| ------------------------------------------------------------ | -------------------------------------- | ---------------- |
| 显示所有符号  Show all Symbols                               | Command⌘ + T                           | Ctrl + T         |
| 转到行... Go  to Line...                                     | Command⌘ + G                           | Ctrl + G         |
| 转到文件... Go  to File...                                   | Command⌘ + P                           | Ctrl + P         |
| 转到符号... Go  to Symbol...                                 | Command⌘ + Shift⇧ + O                  | Ctrl + Shift + O |
| 转到下一个错误或警告  Go to next error or warning            | F8                                     | F8               |
| 转到上一个错误或警告  Go to previous error or warning        | Shift⇧ + F8                            | Shift + F8       |
| 🦋**返回/前进 Go  back / forward**🦋                           | `Control⌃ + -`/`Control⌃ + Shift⇧ + -` | Alt + ←/→        |
| 切换选项卡移动焦点  Toggle Tab moves focus                   | Command⌘ + Shift⇧ + M                  | Ctrl + M         |
| **Uppercase/Lowercase**                                      | Command⌘ + U/Shift⇧ + Command⌘ + U     |                  |
|                                                              |                                        |                  |
| 在上/下插入光标  Insert cursor above / below                 | Option⌥ + Command⌘ + ↑/↓               | Ctrl + Alt +↑/↓  |
| 撤消上一个光标操作  Undo last cursor operation               |                                        | Ctrl + U         |
| **选择当前选择的所有出现<br>Select all occurrences of Find Match** | Shift⇧ + Command⌘ + L                  | Ctrl + Shift + L |
| **选择当前字的所有出现<br>Change all occurrences**           | Command⌘ + F2                          | Ctrl + F2        |
| **Copy Line Down/Up**                                        | Option⌥ + Shift⇧ + ↑/↓                 |                  |
| **Move Line Down/Up**                                        | Option⌥ + ↑/↓                          |                  |



# WIN7 ctrl + space 快捷键冲突修改 

You need to use regedit to do this.

Step 1.
Go to start and type "regedit" in the blank space ("search" under Win 7 or "run" under Win XP)

Step 2:
Go to HKEY_CURRENT_USER/Control Panel/Input Method/Hot Keys
00000010 is for Ime/NonIme Toggle,
00000011 is for Shape Toggle
00000012 is for Punctuation 'Toggle

Step 3:
Go whichever hot key you want to modify, right click on the item and select modify,  here are the rules:
Key Modifiers: 
00 C0 00 00, no "control" or "shift" or "Alt"  (Set this value if you don't need the hot key)
01 C0 00 00, "left Alt"
02 C0 00 00, shift
04 C0 00 00, control
06 C0 00 00, control+shift
Or combination of the above to make your own.

Virtual Key:
the actual key combination, ascii code
20 00 00 00, for space
21 00 00 00, for Page_Up
00 00 00 00, for no key
ff 00 00 00, for NONE!  (Set this value if you don't need the hot key)



# Less命令

less 与 more 类似，less 可以随意浏览文件，支持翻页和搜索，支持向上翻页和向下翻页。

```shell
less [参数] 文件 
```

**参数说明**：

- -b <缓冲区大小> 设置缓冲区的大小

- -e 当文件显示结束后，自动离开

- -f 强迫打开特殊文件，例如外围设备代号、目录和二进制文件

- -g 只标志最后搜索的关键词

- -i 忽略搜索时的大小写

- **-m 显示类似more命令的百分比**

- -N 显示每行的行号

- -o <文件名> 将less 输出的内容在指定文件中保存起来

- -Q 不使用警告音

- -s 显示连续空行为一行

- -S 行过长时间将超出部分舍弃

- -x <数字> 将"tab"键显示为规定的数字空格

- **/字符串：向下搜索"字符串"的功能**

- **?字符串：向上搜索"字符串"的功能**

- **n：重复前一个搜索（与 / 或 ? 有关）**

- **N：反向重复前一个搜索（与 / 或 ? 有关）**

- **b 向上翻一页**

- **d 向后翻半页**

- **G - 移动到最后一行**

- **g - 移动到第一行**

- **q / ZZ - 退出 less 命令**

- 👉 **可以按大写 F，就会有类似 tail -f 的效果，读取写入文件的最新内容， 按 ctrl+C 停止。**

- 👉**可以按 v 进入编辑模型， shift+ZZ 保存退出到 less 查看模式。**

- 👉**可以按 :e 查看下一个文件， 用 :n 和 :p 来回切换。**

- h 显示帮助界面

- **Q 退出less 命令**

- u 向前滚动半页

- y 向前滚动一行

- 空格键 滚动一页

- 回车键 滚动一行

- [pagedown]： 向下翻动一页

- [pageup]： 向上翻动一页



# du -sh file_path占用磁盘空间大小

**Explanation**

- `du` (**d**isc **u**sage) command estimates file_path space usage

- The options `-sh` are (from `man du`):

  ```
    -s, --summarize
           display only a total for each argument
  
    -h, --human-readable
           print sizes in human readable format (e.g., 1K 234M 2G)
  
  #查看当前文件夹下所有文件大小
  du -sh */
  ```
  
  To check more than one directory and see the total, use `du -sch`:
  
  ```
    -c, --total
           produce a grand total
  ```
