[![Kotlin](https://img.shields.io/badge/Kotlin-1.6.10-blue.svg)](http://kotlinlang.org)
![Tests](https://github.com/landgrafhomyak/itmo-bevm/actions/workflows/test.yml/badge.svg)
[![JitPack](https://jitpack.io/v/landgrafhomyak/itmo-bevm.svg)](https://jitpack.io/#landgrafhomyak/itmo-bevm)
![CLI](https://github.com/landgrafhomyak/itmo-bevm/actions/workflows/cli.yml/badge.svg)

[![Kotlin/MPP](https://img.shields.io/badge/Kotlin/MPP-7F52FF.svg)](https://kotlinlang.org/docs/multiplatform.html)

# Базовая электронно-вычислительная машина

Альтернативная реализация БЭВМ для [ИТМО](https://itmo.ru)

# CLI

## Установка

Готовые бинарники для некоторых операционных систем есть
на [странице релиза](https://github.com/landgrafhomyak/itmo-bevm/releases/tag/v0.0b1)

Для остальных операционных систем:

### Linux

```shell
git clone https://github.com/landgrafhomyak/itmo-bevm tmp
cd tmp
git checkout v0.0b1
./gradlew collectCli
cd dist
sudo mv bevm.kexe /bin/bevm
```

### Windows

```shell
git clone https://github.com/landgrafhomyak/itmo-bevm tmp
cd tmp
git checkout v0.0b1
./gradlew collectCli
cd dist
move /-Y bevm.exe "C:/Program Files/"
```

### Mac OS

```shell
git clone https://github.com/landgrafhomyak/itmo-bevm tmp
cd tmp
git checkout v0.0b1
./gradlew collectCli
cd dist
# bevm.kexe
```
## Использование

Выводит содержимое бинарного файла в консоль для удобного просмотра

`-l <беззнаковое число>` количество записей в одной строке

`-o <файл>` дополнительно сохраняет вывод в файл

`-w` склеивает байты по 2 (для представления бэвм)

```shell
bevm viewbin -w -l 4 somebinfile.bin
# 0x0 | 0000 0000 afc5 e000
# 0x4 | afff 0680 3fff e001
# 0x8 | 0740 2000 e000 0100
```

---
Загружает образ памяти из файла (при необходимости дополняя нулями в конец), запускает выполнение в указанном месте и
выводит дамп памяти в формате аналогичном команде 'bevm viewbin'

`-l <беззнаковое число>` количество записей в одной строке

`-o <файл>` дополнительно сохраняет дамп памяти в бинарном виде в файл

`-ip <беззнаковое число>` начальное значение регистра IP (указатель на начало программы в бинарном файле), по умолчание
равен 0

```shell
bevm run -l 4 -ip 2 somebinfile.bin
# 0x0 | 00c4 ffff afc5 e000
# 0x4 | afff 0680 3fff e001
# 0x8 | 0740 2000 e000 0100
```

---
Загружает образ памяти из файла (при необходимости дополняя нулями в конец), запускает выполнение в указанном месте и
выводит трассировку программы

`-o <файл>` дополнительно сохраняет трассировку в файл

`-ip <беззнаковое число>` начальное значение регистра IP (указатель на начало программы в бинарном файле), по умолчание
равен 0

```shell
bevm trace -ip 2 somebinfile.bin
#  addr   cmd  |  AC   DR   BR   CR   PS   IP   IR   SP   AR  C V Z N |
# 0x002 - afc5 | 00c5 00c5 00c5 afc5 0080  003 0000  000  002 0 0 0 0 | LD #0xc5
# 0x003 - e000 | 00c5 00c5 0003 e000 0080  004 0000  000  000 0 0 0 0 | ST 0x000
# 0x004 - afff | 00ff 00ff 00ff afff 0080  005 0000  000  004 0 0 0 0 | LD #0xff
# 0x005 - 0680 | ff00 0680 0005 0680 0088  006 0000  000  005 0 0 0 + | SWAB
# 0x006 - 3fff | ffff ff00 00ff 3fff 0088  007 0000  000  006 0 0 0 + | OR #0xff
# 0x007 - e001 | ffff ffff 0007 e001 0088  008 0000  000  001 0 0 0 + | ST 0x001
# 0x008 - 0740 | fffe 0740 0008 0740 0089  009 0000  000  008 + 0 0 + | DEC
# 0x009 - 2000 | 00c4 00c5 0009 2000 0081  00a 0000  000  000 + 0 0 0 | AND 0x000
# 0x00a - e000 | 00c4 00c4 000a e000 0081  00b 0000  000  000 + 0 0 0 | ST 0x000
# 0x00b - 0100 | 00c4 0100 000b 0100 0081  00c 0000  000  00b + 0 0 0 | HLT
```
