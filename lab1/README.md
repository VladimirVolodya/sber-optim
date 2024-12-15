# optimization-mipt Lab1

## Предподготовка

Запуск всех заданий, за исключением `PrematurePromotion`, производился на `8` версии java: java-1.8.0-openjdk.x86_6.
`PrematurePromotion` запускался на `21` версии java: java-21-openjdk.x86_64.

## Оглавление

- [Пункт 1. MaxMemory](#MaxMemory)
    - [Подпункт 1.1. Флаг -Xmx512m](#Xmx512m)
    - [Подпункт 1.2. Флаг -Xmx512m](#Xms512m)
    - [Подпункт 1.3. Флаг -XX:+PrintGCDetails](#PrintGCDetails)
    - [Подпункт 1.4. Флаг -XX:+SurvivorRatio](#SurvivorRatio)
    - [Подпункт 1.5. Флаг -XX:+UseG1GC](#UseG1GC)
- [Пункт 2. PhantomReferences](#PhantomReferences)
    - [Подпункт 2.1. Без дополнительных настроек](#NoAdditionalSettingsPhantomReferences)
    - [Подпункт 2.2. Флаг -Dphantom.refs=true](#phantom.refs)
    - [Подпункт 2.3. Флаг -Dphantom.refs=true, увеличенная память](#phantom.refs-increased-limits)
    - [Подпункт 2.4. Флаг -Dno.ref.clearing=true](#no.ref.clearing)
- [Пункт 3. PrematurePromotion](#PrematurePromotion)
    - [Подпункт 3.1. Без дополнительных настроек](#NoAdditionalSettingsPrematurePromotion)
    - [Подпункт 3.2. Ограничение памяти](#LimitedMem)
    - [Подпункт 3.3. Флаг -Xlog:gc=debug:file=gc.txt](#log.gc)
    - [Подпункт 3.4. Флаг -Dmax.chunks=1000](#max.chunks)
    - [Подпункт 3.5. Флаг -XX:+NeverTenure](#NeverTenure)
- [Пункт 4. SoftReferences](#SoftReferences)
    - [Подпункт 4.1. Без дополнительных настроек](#NoAdditionalSettingsSoftReferences)
    - [Подпункт 4.2. Флаг -Dsoft.refs=true](#soft.refs)
    - [Подпункт 4.3. Флаг -Dsoft.refs=true увеличенная память](#soft.refs-increased-limits)
- [Пункт 5. WeakReferences](#WeakReferences)
    - [Подпункт 5.1. Без дополнительных настроек](#NoAdditionalSettingsWeakReferences)
    - [Подпункт 5.2. Флаг -Dweak.refs=true](#weak.refs)
    - [Подпункт 5.3. Флаг -Dweak.refs=true, увеличенная память](#weak.refs-increased-limits)


<h2 id="MaxMemory">Пункт 1. MaxMemory</h2>

<h3 id="Xmx512m">Подпункт 1.1. Флаг -Xmx512m</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ java -Xmx512m -jar target/optdemo-0.0.1-SNAPSHOT.jar
Max memory: 477 MB
```

Флаги:
 - Флаг `-Xmx512m` определяет максимальный размер динамической памяти (heap) для java-приложения как `512 MB`.

Комментарии:
 - Реальное значение памяти, доступной приложению слегка меньше, так как JVM резервирует небольшой объем памяти для внутренних задач. Таких как аллоцирование новых объектов или сборка мусора. Также доступная память может уменьшаться из-за выравнивания.

<h3 id="Xms512m">Подпункт 1.2. Флаг -Xms512m</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ java -Xmx512m -Xms512m -jar target/optdemo-0.0.1-SNAPSHOT.jar
Max memory: 491 MB
```

Флаги:
 - Флаг `-Xms512m` определяет минимальный (в т.ч. изначальный, доступный на старте приложжения) размер динамической памяти (heap).

Комментарии:
 - Реальное значение доступной памяти слегка увеливается, так как JVM теперь аллоцирует заранее весь необходимый объем, однако значение все еще меньше `512 MB` по описанным в предыдущем пункте причинам.

<h3 id="PrintGCDetails">Подпункт 1.3. Флаг -XX:+PrintGCDetails</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ java -Xmx512m -Xms512m -XX:+PrintGCDetails -jar target/optdemo-0.0.1-SNAPSHOT.jar
Max memory: 491 MB
Heap
 PSYoungGen      total 153088K, used 13227K [0x00000000f5580000, 0x0000000100000000, 0x0000000100000000)
  eden space 131584K, 10% used [0x00000000f5580000,0x00000000f626ae48,0x00000000fd600000)
  from space 21504K, 0% used [0x00000000feb00000,0x00000000feb00000,0x0000000100000000)
  to   space 21504K, 0% used [0x00000000fd600000,0x00000000fd600000,0x00000000feb00000)
 ParOldGen       total 349696K, used 0K [0x00000000e0000000, 0x00000000f5580000, 0x00000000f5580000)
  object space 349696K, 0% used [0x00000000e0000000,0x00000000e0000000,0x00000000f5580000)
 Metaspace       used 5623K, capacity 5824K, committed 6016K, reserved 1056768K
  class space    used 551K, capacity 617K, committed 640K, reserved 1048576K
```

Флаги:
 - Флаг `-XX:+PrintGCDetails` выводит детальную информацию о сборке мусора и использовании динамической памяти.

Комментарии:
 - В логах GC видим, что eden space занимает `149.5 MB`, две области survivor space (from и to) - по `21 MB` каждая. Old gen аллоцирует `341.5 MB`.
 - Объекты могут одновременно существовать лишь в eden space, old generation space и одном из двух регионов survivor space. Три этих региона памяти в сумме и дают указанные в настройках JVM `512 MB`, однако объем реально доступной памяти - это сумма eden space и old generation space. Видимо, это вызвано тем, что долгое отсутствие новые объектов переведет все объекты в old generation, после чего приложение напрямую сможет заполнить только eden space.

<h3 id="SurvivorRatio">Подпункт 1.4. Флаг -XX:+SurvivorRatio</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ java -Xms512m -Xmx512m -XX:SurvivorRatio=100 -jar target/optdemo-0.0.1-SNAPSHOT.jar
Max memory: 510 MB
```

Флаги:
 - Флаг `-XX:SurvivorRatio=100` устанавливает отоношение размера каждого из двух регионов survivor space к размеру eden space равным `1/100`, что увеличивает размер eden space.

Комментарии:
 - Как следствие, увеличивается сумма размеров регионов eden space и old generation space, что мы и видим в выводе программы.



<h3 id="UseG1GC">Подпункт 1.5. Флаг -XX:+UseG1GC</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ java -Xmx512m -XX:+UseG1GC -jar target/optdemo-0.0.1-SNAPSHOT.jar
Max memory: 512 MB
```

Флаги:
 - Флаг `-XX:+UseG1GC` выбирает G1 GC, оптимизированный под приложения, требущие быстрого отклика и высокой пропускной способности.

Комментарий:
 - Он делит всю доступную память на множество регионов, каждый из которых принадлежать любому из трех типов: eden, survivor, old generation. По этой причине приложению становится в полном объеме доступна вся выделенная JVM память, что и видно в выводе программы.


<h2 id="PhantomReferences">Пункт 2. PhantomReferences</h2>

<h3 id="NoAdditionalSettingsPhantomReferences">Подпункт 2.1. Без дополнительных настроек</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 15 java -verbose:gc -Xmx24m -XX:NewSize=16m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSHOT.jar > log2-1.txt
vsm@fedora:~/VSCodeProjects/optimization-labs$ wc -l log2-1.txt
1743 log2-1.txt
```

[log2-1.txt](log2/log2-1.txt)

Флаги:
 - Флаг `-XX:MaxTenuringThreshold=1` устанавливает максимальное количество эпох нахождения объекта в young generatation равным `1`, объексты быстро перемещаются в old generation, заполняя его.
 - Флаг `-XX:-UseAdaptiveSizePolicy` отключает выделение новых массивов памяти в young и old generation.
 - Флаг `-Xmx24m` устанавливает максимальный объем памяти доступный JVM равным `24 MB`.
 - Флаг `-XX:NewSize=16m `  устанавливает объем памяти, выделенный под young generation равным `16 MB`.

Комментарии:
 - Под young и old generation выделено очень мало памяти, возможность расширения этих регионов отключена, из-за `MaxTenuringThreshold=1` old generation быстро заполняется.
 - В результате весь доступный объем памяти быстро исчерпывается, ссылки на объекты очищаются как только достигается лимит в `BUFFER_SIZE=24*1024` объектов, что вызывает частые очистки памяти (в подавляющем большинстве случаев из-за `Allocation Failure`, так как young generation быстро исчерпывается).


<h3 id="phantom.refs">Подпункт 2.2. Флаг -Dphantom.refs=true</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 15 java -Dphantom.refs=true -verbose:gc -Xmx24m -XX:NewSize=16m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSHOT.jar > log2-2.txt
vsm@fedora:~/VSCodeProjects/optimization-labs$ wc -l log2-2.txt
916 log2-2.txt
```

[log2-2.txt](log2/log2-2.txt)

Флаги:
 - Флаг `-Dphantom.refs=true` меняет поведение программы, теперь на каждый объект создается фантомная ссылка, что увечивает среднее время жизни объектов.

Комментарии:
 - Объект, на который создана фантомная ссылка, не будет очищен, пока у ссылки не будет вызван метод `clear()`. Это, как уже было сказано, увеличивает время жизни объектов, объектов. Объекты, для которых доступна чистка, появляются не так быстро, что уменьшает число вызовов GC почти вдвое.
 - В логах чаще становятся заметны полные очистки, как раз из-за увеличения времени жизни объектов. Однако подавляющее большинство все еще за быстрыми очистками.

<h3 id="phantom.refs-increased-limits">Подпункт 2.3. Флаг -Dphantom.refs=true, увеличенная память</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 15 java -Dphantom.refs=true -verbose:gc -Xmx64m -XX:NewSize=32m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSHOT.jar > log2-3.txt
vsm@fedora:~/VSCodeProjects/optimization-labs$ wc -l log2-3.txt
831 log2-3.txt
```

[log2-3.txt](log2/log2-3.txt)

Комментарий:
 - Увеличение доступных регионам памяти объемов (`-Xmx64m -XX:NewSize=32m`) еще уменьшает частоту вызовов GC, так как заполняются регионы теперь медленее.
 - С увеличенными объемами регионов увеливается промежуток времени между чистками, фантомные ссылки чаще успевают удалиться (методом `clear()`) за время нахождения в young generation, так что в логах вновь видны только быстрые очистки.

<h3 id="no.ref.clearing">Подпункт 2.4. Флаг -Dno.ref.clearing=true</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 15 java -Dphantom.refs=true -Dno.ref.clearing=true -verbose:gc -Xmx64m -XX:NewSize=32m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSHOT.jar > log2-4.txt 
vsm@fedora:~/VSCodeProjects/optimization-labs$ wc -l log2-4.txt
119 log2-4.txt
```

[log2-4.txt](log2/log2-4.txt)

Флаги:
 - Флаг `-Dno.ref.clearing=true` меняет поведение программы, отключая вызов метода `clear()` у фантомной ссылки, в результате выделенная память не возвращается JVM.

Комментарии:
 - Отсутствие очисток ведет к заполнению old generation. Теперь примерно половина всех чисток - полные.
 - Бесконечное время жизни объектов ведет к существенному уменьшению частоты чисток. По сравнению с первым запуском частота уменьшилась примерно в `15` раз.
 - Рано или поздно приведет к `OutOfMemory`.


<h2 id="PrematurePromotion">Пункт 3. PrematurePromotion</h2>

Использована `21` версия java.

<h3 id="NoAdditionalSettingsPrematurePromotion">Подпункт 3.1. Без дополнительных настроек</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 30 java -verbose:gc -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSHOT.jar > log3-1.txt
```

[log3-1.txt](log3/log3-1.txt)

Комментарии:
 - В качестве GC по умолчанию используется G1.
 - Флаги уже были разобраны в предыдущих пунктах. `MaxTenuringThreshold=1` должен вынуждать JVM быстро перемещать объекты в old generation, но размеры регионов не ограничены специально, и системе хватает значений по умолчанию для того, чтобы они не заполнялись слишком быстро, приводя к перемещению в old generation.
 - Чистки происходят не слишком часто и только быстрые (Pause Young).

<h3 id="LimitedMem">Подпункт 3.2. Ограничение памяти</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 30 java -verbose:gc -Xmx24m -XX:NewSize=16m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSHOT.jar > log3-2.txt
```

[log3-2.txt](log3/log3-2.txt)

Комментарии:
 - В отличие от предыдущего запуска чистки происходят гораздо чаще, в том числе full (теперь на одну full очистку приходится около двух young). Связано это с уменьшением памяти, доступной JVM. Young generation быстрее заполняется, что, с одной стороны, приводит к более частым быстрым очисткам, с другой, способствует быстрому перетеканию объектов в old generation, то есть увеличивает частоту и полных очисток.
 - Быстрые очистки чаще теперь выступают как части Mixed очисток, в отличие от предыдущих, где это были Normal очистки.

<h3 id="log.gc">Подпункт 3.3. Флаг -Xlog:gc=debug</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 30 java -Xlog:gc=debug -Xmx64m -XX:NewSize=32m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSHOT.jar > log3-3.txt
```

[log3-3.txt](log3/log3-3.txt)

Флаги:
 - Флаг -Xlog:gc=debug включает debug-режим логгирования GC.

Комментарии:
 - Увеличение памяти в сравнении с предыдущим пунктом существенно улучшило ситуацию. Сборки происходят гораздо реже, вновь видны только Normal Young сборки.
 - debug-режим показал, что JVM выделяет `3` потока на конкурентные этапы сборки и `10` на параллельные. Также видны начальный и максимальный размеры mark стека (структуры, используемой для хранения ссылок помеченных к удалению).

<h3 id="max.chunks">Подпункт 3.4. Флаг -Dmax.chunks=1000</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 30 java -Dmax.chunks=1000 -verbose:gc -Xmx24m -XX:NewSize=16m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSHOT.jar > log3-4.txt
```

[log3-4.txt](log3/log3-4.txt)

Флаги:
 - Флаг `-Dmax.chunks=1000` уменьшает верхнюю границу количества чанков в программе для начала их обработки и освобождения. В результате очистки происходят чаще и нагрузка на память уменьшается.

Комментарии:
 - Число чанков уменьшилось по сравнению со значением по умолчанию в `10` раз, а лимиты памяти в `2-3` раза, так что в относительных пропорциях уменьшение все еще существенное, young generation все еще достаточно, происходят лишь быстрые очистки.
 - Видно, что теперь сборки начинаются не с `45-50 MB` занятой памяти, а стабильно с `17 MB`, что вызвано, скорее всего, именно уменьшением лимитов памяти.

<h3 id="NeverTenure">Подпункт 3.5. Флаг -XX:+NeverTenure</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 30 java -verbose:gc -Xmx64m -XX:NewSize=32m -XX:+NeverTenure -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSHOT.jar > log3-5.txt
```

[log3-5.txt](log3/log3-5.txt)

Флаги:
 - Флаг `-XX:+NeverTenure` запрещает переход объектов из young в old generation. 

Комментарии:
 - Запрет перехода в old generation потенциально увеличивает нагрузку на young generation, однако объекты не выходили за его пределы и в прошлом пункте, не переходят и сейчас, тем более лимиты памяти увеличены.
 - Можно также сделать вывод о том, что объекты не живут достаточно времени для перехода в old generation, так как серьезного увеличения частоты сборки мусора не наблюдается. Но этот вывод можно было сделать и из предыдущих пунктов.
 - Однако, пауза в среднем увеличилась в два раза, что говорит об увеличении количества объектов в young generation в сравнении с предыдущим пунктом.


<h2 id="SoftReferences">Пункт 4. SoftReferences</h2>

<h3 id="NoAdditionalSettingsSoftReferences">Подпункт 4.1. Без дополнительных настроек</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 15 java -verbose:gc -Xmx24m -XX:NewSize=16m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSHOT.jar > log4-1.txt
vsm@fedora:~/VSCodeProjects/optimization-labs$ wc -l log4-1.txt 
866 log4-1.txt
```

[log4-1.txt](log3/log4-1.txt)

Комментарии:
 - В исполняемом коде постоянно создаются и удаляются объекты, что создает высокую нагрузку на память, что вкупе с малым объемом выделенной JVM памяти, быстрым переходом в old generation и запретом на дополнительную аллокацию памяти делает частоту вызовов GC слишком большой (порядка 800 за 15 секунд).
 - В логе встречаются как быстрые, так и полные очистки. Частота полных очисток меньше частоты быстрых в `50-100` раз.

<h3 id="soft.refs">Подпункт 4.2. Флаг -Dsoft.refs=true</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 15 java -Dsoft.refs=true -verbose:gc -Xmx24m -XX:NewSize=16m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSHOT.jar > log4-2.txt
vsm@fedora:~/VSCodeProjects/optimization-labs$ wc -l log4-2.txt 
473 log4-2.txt
```

[log4-2.txt](log3/log4-2.txt)

Флаги:
 - Флаг `-Dsoft.refs=true` меняет поведение программы, теперь на каждый объект создается soft-ссылка. Soft-ссылки сохраняются в массив размера `24 * 1024` элементов. Когда массив заполняется до предела, он очищается.

Комментарии:
 - Наличие soft-ссылок увеличивает время жизни объекта, но наличие на объект лишь soft-ссылок не гарантирует его дальнейшее существование. По умолачнию soft-ссылка на объект прололжает существовать после удаления strong ссылок 1 секунду за каждый мегабайт свободного места.
 - Частота соборок уменьшилась в `2` раза, однако теперь доля полных очисток существенно возросла, потому что с увеличением времени жизни объектов они чаще стали перетекать в old generation.

<h3 id="soft.refs-increased-limits">Подпункт 4.3. Флаг -Dsoft.refs=true увеличенная память</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 15 java -Dsoft.refs=true -verbose:gc -Xmx64m -XX:NewSize=32m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSHOT.jar > log4-3.txt
vsm@fedora:~/VSCodeProjects/optimization-labs$ wc -l log4-3.txt 
501 log4-3.txt
```

[log4-3.txt](log3/log4-3.txt)

Комментарии:
 - С увеличение доступной JVM памяти объекты уже не так активно перетекают в old generation, так как успевают "отмереть" в young generation, в результате  доля полных очисток уменьшилась.
 - Полные очистки происходят реже, в old generation копится больше данных, следовательно, уменьшается время жизни объектов с наличием лишь soft-ссылок, такие объекты чаще отмирают, что приводит к более частым быстрым очисткам. По этой причине общее число чисток практически не изменилось (увеличение несущественно, в пределах погрешсти).

<h2 id="WeakReferences">Пункт 5. WeakReferences</h2>

<h3 id="NoAdditionalSettingsWeakReferences">Подпункт 5.1. Без дополнительных настроек</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 15 java -verbose:gc -Xmx24m -XX:NewSize=16m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSHOT.jar > log5-1.txt
vsm@fedora:~/VSCodeProjects/optimization-labs$ wc -l log5-1.txt 
741 log5-1.txt
```

[log5-1.txt](log3/log5-1.txt)

Комментарии:
 - Код по существу отличается от аналогичного из [пункта 4.1](#NoAdditionalSettingsSoftReferences) лишь тем, что здесь создаются слабые ссылки на объект. При этом флаг создания таких ссылок установлен в значение `false`, то есть результат ожидаемо схож с результатом [пункта 4.1](#NoAdditionalSettingsSoftReferences)

<h3 id="weak.refs">Подпункт 5.2. Флаг -Dweak.refs=true</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 15 java -Dweak.refs=true -verbose:gc -Xmx24m -XX:NewSize=16m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSH
OT.jar > log5-2.txt
vsm@fedora:~/VSCodeProjects/optimization-labs$ wc -l log5-2.txt 
784 log5-2.txt
```

[log5-2.txt](log3/log5-2.txt)

Флаги:
 - Флаг `-Dweak.refs=true` меняет поведение программы, теперь на каждый объект создается weak-ссылка. Weak-ссылки сохраняются в массив размера `24 * 1024` элементов. Когда массив заполняется до предела, он очищается.

Комментарии:
 - В отличие от soft-ссылки weak версия практически никак не увеличивает время жизни объекта, в результате существенных изменений не видно. Текущие можно списать на погрешность.

<h3 id="weak.refs-increased-limits">Подпункт 5.3. Флаг -Dweak.refs=true, увеличенная память</h3>

Результат запуска:
```
vsm@fedora:~/VSCodeProjects/optimization-labs$ timeout 15 java -Dweak.refs=true -verbose:gc -Xmx64m -XX:NewSize=32m -XX:MaxTenuringThreshold=1 -XX:-UseAdaptiveSizePolicy -jar target/optdemo-0.0.1-SNAPSH
OT.jar > log5-3.txt
vsm@fedora:~/VSCodeProjects/optimization-labs$ wc -l log5-3.txt 
676 log5-3.txt
```

[log5-3.txt](log3/log5-3.txt)

Комментарии:
 - Как уже говорилось, weak-ссылки практически не оказывают влияния на время жизни объекта. Поэтому уменьшение числа очисток скорее вызвано уже описанными ранее причинами, связанными с увеличением доступного JVM места.
 - Доля полных очисток упала до нуля аналогично предыдущим пунктам. Связи с наличием weak-ссылок не наблюдается.
