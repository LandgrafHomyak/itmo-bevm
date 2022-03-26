[![Kotlin](https://img.shields.io/badge/Kotlin-1.6.10-blue.svg)](http://kotlinlang.org)
![Tests](https://github.com/landgrafhomyak/itmo-bevm/actions/workflows/test.yml/badge.svg)
[![JitPack](https://jitpack.io/v/landgrafhomyak/itmo-bevm.svg)](https://jitpack.io/#landgrafhomyak/itmo-bevm)
![CLI](https://github.com/landgrafhomyak/itmo-bevm/actions/workflows/cli.yml/badge.svg)

# Базовая электронно-вычислительная машина

Альтернативная реализация БЭВМ для [ИТМО](https://itmo.ru)

# CLI

## Установка

Готовые бинарники для некоторых операционных систем есть
на [странице релиза](https://github.com/landgrafhomyak/itmo-bevm/releases/tag/v0.0b0)

Для остальных операционных систем:

### POSIX

```shell
git clone https://github.com/landgrafhomyak/itmo-bevm tmp
cd tmp
git checkout v0.0b0
./gradlew collectCli
cd dist
sudo mv bevm /bin
```

### Windows

```shell
git clone https://github.com/landgrafhomyak/itmo-bevm tmp
cd tmp
git checkout v0.0b0
./gradlew collectCli
cd dist
move /-Y bevm.exe "C:/Program Files/"
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