# RxKotlinExample

```kotlin
val observable1 = Observable.create<Int> {
    it.onNext(1)
    it.onNext(2)
    it.onComplete()
}
val observer: Observer<Int> = object : Observer<Int> {
    override fun onComplete() = println("onComplete()")
    override fun onNext(item: Int) = println("onNext() - $item")
    override fun onError(e: Throwable) = println("onError() - ${e.message}")
    override fun onSubscribe(d: Disposable) = println("onSubscribe() - $d ")
}

observable1.subscribe(observer)
```

```shell
onSubscribe() - CreateEmitter{null}
onNext() - 1
onNext() - 2
onComplete()
```

```kotlin
// list와 같이 iterable을 지원하는 instance를 Observable 형태로 변경합니다.
val list = listOf(1,2,3)
val listOb = Observable.fromIterable(list)

// Callable 객체를 Observable 형태로 변경합니다.
val call = Callable<Int> { 4 }
val callOb = Observable.fromCallable(call)

// Future 객체를 Observable 형태로 변경합니다.
val future = object : Future<Int> {
    override fun get() = 5
    override fun get(timeout: Long, unit: TimeUnit) = 6
    override fun isDone() = true
    override fun cancel(mayInterruptIfRunning: Boolean) = false
    override fun isCancelled() = false
}

val futureOb = Observable.fromFuture(future)

val observer: Observer<Int> = object: Observer<Int> {
    override fun onComplete() = println("onComplete()")
    override fun onNext(item: Int) = println("onNext() - $item")
    override fun onError(e: Throwable) = println("onError() - ${e.message}")
    override fun onSubscribe(d: Disposable) = println("onSubscribe() - $d ")
}

listOb.subscribe(observer)
callOb.subscribe(observer)
futureOb.subscribe(observer)
```


```shell
onSubscribe() - io.reactivex.internal.operators.observable.ObservableFromIterable$FromIterableDisposable@4405846
onNext() - 1
onNext() - 2
onNext() - 3
onComplete()
onSubscribe() - 0
onNext() - 4
onComplete()
onSubscribe() - 0
onNext() - 5
onComplete()
```

```kotlin
val list = listOf(1,2,3)
val num = 3
val str = "Wow!"
val map = mapOf(1 to "one", 2 to "two")

// just 메소드는 받은 인자를 그대로 전달한다.
// list든 map든 받은 객체 자체를 전달하며, 여러 개를 전달하려면 각각의 인자로 넣어서 호출해야 한다.
val justOb = Observable.just(list, num, str, map)

val observer: Observer<Any> = object: Observer<Any> {
    override fun onComplete() = println("onComplete()")
    override fun onNext(item: Any) = println("onNext() - $item")
    override fun onError(e: Throwable) = println("onError() - ${e.message}")
    override fun onSubscribe(d: Disposable) = println("onSubscribe() - $d ")
}

justOb.subscribe(observer)
```

```shell
onSubscribe() - io.reactivex.internal.operators.observable.ObservableFromArray$FromArrayDisposable@90bce59
onNext() - [1, 2, 3]
onNext() - 3
onNext() - Wow!
onNext() - {1=one, 2=two}
onComplete()
```

```kotlin
val observer = object: Observer<Int> {
    override fun onComplete() = println("onComplete()")
    override fun onNext(item: Int) = println("onNext() - $item")
    override fun onError(e: Throwable) = println("onError() - ${e.message}")
    override fun onSubscribe(d: Disposable) = println("onSubscribe() - $d ")
}

Observable.range(1,3).subscribe(observer)

Observable.empty<Int>().subscribe(observer)

val observer1 = object: Observer<Long> {
    override fun onComplete() = println("onComplete()")
    override fun onNext(item: Long) = println("onNext() - $item")
    override fun onError(e: Throwable) = println("onError() - ${e.message}")
    override fun onSubscribe(d: Disposable) = println("onSubscribe() - $d ")
}

Thread { Observable.interval(1000L, TimeUnit.MILLISECONDS).subscribe(observer1)}.start()

val th1 = Thread { Thread.sleep(3000L)}
th1.start()
th1.join()
```

```shell
onSubscribe() - 0
onNext() - 1
onNext() - 2
onNext() - 3
onComplete()
onSubscribe() - INSTANCE
onComplete()
onSubscribe() - null
onNext() - 0
onNext() - 1
.
.
.
```

```kotlin
val observer1 = object: Observer<Long> {
    override fun onComplete() = println("onComplete()")
    override fun onNext(item: Long) = println("onNext() - $item")
    override fun onError(e: Throwable) = println("onError() - ${e.message}")
    override fun onSubscribe(d: Disposable) = println("onSubscribe() - $d ")
}

Thread {Observable.timer(1000L,TimeUnit.MILLISECONDS).subscribe(observer1)}.start()

val th1 = Thread { Thread.sleep(3000L)}
th1.start()
th1.join()
```

```shell
onSubscribe() - null
onNext() - 0
onComplete()
```

```kotlin
val observer = Observable.range(1,3)

observer.subscribe(
    { item -> println("onNext - $item")},
    { err -> println("onError - $err")},
    { println("onComplete()")},
    { d -> println("onSubscribe() - $d")}
)
```

```shell
onSubscribe() - 0
onNext() - 1
onNext() - 2
onNext() - 3
onComplete()
```

```kotlin
val observable = Observable.interval(1000L, TimeUnit.MILLISECONDS)

val observer = object: Observer<Long> {
    lateinit var disposable: Disposable

    override fun onComplete() = println("onComplete()")
    override fun onNext(item: Long) {
        println("onNext() - $item")
        if (item >= 5 && !disposable.isDisposed) {
            disposable.dispose()
        }
    }
    override fun onError(e: Throwable) = println("onError() - ${e.message}")
    override fun onSubscribe(d: Disposable) {
        println("onSubscribe() - $d ")
        disposable = d
    }
}

observable.subscribe(observer)

Thread {
    Thread.sleep(1000L)
}.apply {
    start()
    join()
}
```

```shell
onSubscribe() - null
onNext() - 0
onNext() - 1
onNext() - 2
onNext() - 3
onNext() - 4
onNext() - 5
```

```kotlin
val observable = Observable.interval(1000L, TimeUnit.MILLISECONDS)

val disposable = observable.subscribe(
    { item -> println("onNext - $item")},
    { err -> println("onError - $err")},
    { println("onComplete()")},
    { d -> println("onSubscribe() - $d")}
)

Thread {
    Thread.sleep(2000L)
    disposable.dispose()
    Thread.sleep(1000L)
}.apply {
    start()
    join()
}
```

```shell
onSubscribe() - null
onNext() - 0
onNext() - 1
```

```kotlin
// Hot observable 중에 하나로 connect를 호출하면 배출을 시작합니다.
// 따라서 observer는 subscribe와 상관없이 구독 신청 시점부터 데이터를 전달받습니다.
val connectableObservable = (1..10).toObservable().publish()

// 1번 구독자 등록
connectableObservable.subscribe { println("first subscriber: $it") }
println("Add first subscriber.")

// 2번 구독자 등록
connectableObservable.map { "second subscriber: $it" }.subscribe { println(it) }
println("Add second subscriber.")

// observable connect()
connectableObservable.connect()
// 이 시점부터 구독자의 대한 데이터를 배출한다.

// 3번 구독자 등록.
connectableObservable.subscribe { println("Subscription 3: $it") }
```

```shell
Add first subscriber.
Add second subscriber.
first subscriber: 1
second subscriber: 1
.
.
.
first subscriber: 10
second subscriber: 10
```

```kotlin
val connectableObservable = Observable.interval(1000L, TimeUnit.MILLISECONDS).publish()

// 1번 구독자 등록
connectableObservable.subscribe { println("1st subscriber: $it") }

// 2번 구독자 등록
connectableObservable.map { "2nd subscriber: $it" }.subscribe { println(it) }

// observable connect()
connectableObservable.connect()

runBlocking {
	delay(3000L)
}

// 3번 구독자 등록
connectableObservable.map { "3rd subscriber: $it" }.subscribe{ println(it) }

runBlocking {
	delay(300L)
}
```

```shell
1st subscriber: 0
2nd subscriber: 0

1st subscriber: 1
2nd subscriber: 1

1st subscriber: 2
2nd subscriber: 2

1st subscriber: 3
2nd subscriber: 3
3rd subscriber: 3

1st subscriber: 4
2nd subscriber: 4
3rd subscriber: 4
```



```kotlin
val observable = Observable.interval(1000L, TimeUnit.MILLISECONDS)
val subject = PublishSubject.create<Long>()
observable.subscribe(subject)
runBlocking { delay(3000L) }
subject.subscribe { println("1st: $it") }
runBlocking { delay(3000L) }
subject.subscribe { println("2nd: $it") }
runBlocking { delay(3000L) }
```
<img src="https://t1.daumcdn.net/cfile/tistory/99E3E74F5DE9FFCE03" alt="PublishSubject" style="zoom:50%;" />



```shell
1st: 3
1st: 4
1st: 5
1st: 6
2nd: 6
1st: 7
2nd: 7
1st: 8
2nd: 8
```

```kotlin
val observable = Observable.interval(1000L, TimeUnit.MILLISECONDS)
val subject = BehaviorSubject.create<Long>()
observable.subscribe(subject)
runBlocking { delay(3000L) }
subject.subscribe { println("1st: $it") }
runBlocking { delay(3000L) }
subject.subscribe { println("2nd: $it") }
runBlocking { delay(3000L) }
```

<img src="https://t1.daumcdn.net/cfile/tistory/992EA74F5DE9FFCD31" alt="BehaviorSubject" style="zoom:50%;" />

```shell
// 등록 시점에 이전에 배출된 직전 값 하나를 전달받고 시작한다.
1st: 2
1st: 3
1st: 4
1st: 5
2nd: 5
1st: 6
2nd: 6
1st: 7
2nd: 7
1st: 8
2nd: 8
```

```kotlin
val observable = Observable.just(1,2,3,4,5,6,7,8,9,10)
val subject = AsyncSubject.create<Int>()
observable.subscribe(subject)
subject.subscribe { println("1st: $it") }
subject.subscribe { println("2nd: $it") }
```

```shell
// AsyncSubject는 마지막 값을 한번만 배출한다.
//just를 이용하여 지정된 배출 개수를 갖는 Observable을 생성하여 동작을 확인합니다.
1st: 10
2nd:10
```



<img src="https://t1.daumcdn.net/cfile/tistory/99D3EE4F5DE9FFCD35" alt="AsyncSubject" style="zoom:50%;" />

```kotlin
val observable = Observable.interval(1000L, TimeUnit.MILLISECONDS)
val subject = ReplaySubject.create<Long>()
observable.subscribe(subject)
runBlocking { delay(3000L) }
subject.subscribe { println("1st: $it") }
runBlocking { delay(3000L) }
subject.subscribe { println("2nd: $it") }
runBlocking { delay(300L) }
```

```shell
// ReplaySubject는 cold observable과 유사하게 등록 시점 이전 값을 모두 전달 받은 후 새로 배출되는 값을 전달 받는다.
1st: 0
1st: 1
1st: 2

1st: 3

1st: 4

1st: 5
2nd: 0
2nd: 1
2nd: 2
2nd: 3
2nd: 4
2nd: 5

1st: 6
2nd: 6

1st: 7
2nd: 7

1st: 8
2nd: 8
```

<img src="https://t1.daumcdn.net/cfile/tistory/99FD924F5DE9FFCE33" alt="ReplaySubject" style="zoom:50%;" />

```kotlin
val observer = object: Observer<String> {
    override fun onComplete() = println("onComplete()")
    override fun onNext(item: String) = println("onNext() - $item")
    override fun onError(e: Throwable) = println("onError() - ${e.message}")
    override fun onSubscribe(d: Disposable) = println("onSubscribe() - $d ")
}

val publicSubject = PublishSubject.create<String>()
publicSubject.subscribe(observer)

val asyncSubject = AsyncSubject.create<String>()
asyncSubject.subscribe(observer)

val behaviorSubject = BehaviorSubject.create<String>()
behaviorSubject.subscribe(observer)

val replaySubject = ReplaySubject.create<String>()
replaySubject.subscribe(observer)

(1..3).forEach {
    publicSubject.onNext("public: $it")
    asyncSubject.onNext("async: $it")
    behaviorSubject.onNext("behavior: $it")
    replaySubject.onNext("replay: $it")
}

asyncSubject.onComplete()
```

```shell
// AsyncSubject는 마지막 객체만을 배출하기 때문에 Subject를 사용한다면 명시적으로 onComplete()를 호출해야 마지막값을 전달받을 수 있습니다.

onSubscribe() - flase
onSubscribe() - 0
onSubscribe() - io.reactivex.subjects.BehaviorSubject$BehaviorDisposable@44f1d70
onSubscribe() - 0
onNext() - public: 1
onNext() - behavior: 1
onNext() - replay: 1
onNext() - public: 2
onNext() - behavior: 2
onNext() - replay: 2
onNext() - public: 3
onNext() - behavior: 3
onNext() - replay: 3
onNext() - async: 3
onComplete()
```

```kotlin
runBlocking<Unit>{
    val observable = Observable.range(1,10)
    observable.map {
        val str = "Mapping item $it"
        runBlocking { delay(1000L) }
        println("$str - ${Thread.currentThread().name}")
        str
    }.observeOn(Schedulers.computation())
    .subscribe {
        println("Received $it - ${Thread.currentThread().name}")
        runBlocking { delay(2000L) }
    }
    delay(1000L)
}
```

```shell
// 2000L * 10 millisec
Mapping item 1 - main
Received Mapping item 1 - RxComputationThreadPool-1

Mapping item 2 - main

Mapping item 3 - main
Received Mapping item 2 - RxComputationThreadPool-1

Mapping item 4 - main

Mapping item 5 - main
Received Mapping item 3 - RxComputationThreadPool-1

Mapping item 6 - main

Received Mapping item 4 - RxComputationThreadPool-1
Mapping item 7 - main

Mapping item 8 - main

Received Mapping item 5 - RxComputationThreadPool-1
Mapping item 9 - main

Mapping item 10 - main

Received Mapping item 6 - RxComputationThreadPool-1

Received Mapping item 7 - RxComputationThreadPool-1

Received Mapping item 8 - RxComputationThreadPool-1

Received Mapping item 9 - RxComputationThreadPool-1

Received Mapping item 10 - RxComputationThreadPool-1
// observeOn이 없을 경우엔 // 3000L * 10 millisec
```

```kotlin
// Flowable : backpressure를 지원하는 Observable
// 하나의 Flowable로 128개의 buffer를 지원하여, 생산 속도를 제어할 수 있다.
runBlocking<Unit> {
    val flowable = Flowable.range(1, 150)
    flowable.map {
        println("Mapping item $it")
        it
    }.observeOn(Schedulers.computation())
    .subscribe {
        println("Receive item $it")
        runBlocking { delay(10L) }
    }
    delay(10000L)
}
```

```shell
Mapping item 1
...
Mapping item 128
Receive item 1
...
Receive item 96
Mapping item 129
...
Mapping item 150
Receive item 97
...
Receive item 150
// Flowable은 생산의 갯수가 몇천개 이상으로 많은 때 사용하고 그렇지 않은 경우엔 observable을 사용하는 것이 좋다.
```

```kotlin
runBlocking<Unit>{
    Flowable.range(1,150).map {
        println("Mapping item $it - ${Thread.currentThread().name}")
        it
    }.observeOn(Schedulers.computation())
    .subscribe(ObservableJobs.Companion.MySubscriber())
    delay(5000L)
}

class MySubscriber: Subscriber<Int> {
    override fun onSubscribe(s: Subscription?) {
        s?.request(Long.MAX_VALUE)
    }

    override fun onComplete() = println("onComplete")

    override fun onNext(t: Int?) {
        runBlocking { delay(10) }
        println("OnNext(): $t - ${Thread.currentThread().name}")
    }

    override fun onError(t: Throwable?): Unit = t!!.printStackTrace()
}
```

```shell
Mapping item 1 - main
...
Mapping item 128 - main
OnNext(): 1 - RxComputationThreadPool-1
...
OnNext(): 96 - RxComputationThreadPool-1
Mapping item 129 - main
...
Mapping item 150 - main
OnNext(): 97 - RxComputationThreadPool-1
...
OnNext(): 150 - RxComputationThreadPool-1
onComplete
```

```kotlin
runBlocking<Unit> {
    val flowable = Flowable.create({ view: FlowableEmitter<Int> ->
                                    for (i in 1..200) {
                                        println("send item $i")
                                        view.onNext(i)
                                        runBlocking { delay(10L) }
                                    }
                                   }, BackpressureStrategy.BUFFER)
    val waitingJob = launch { delay(Long.MAX_VALUE) }
    flowable.observeOn(Schedulers.computation()).subscribe(ObservableJobs.Companion.MySubscriber2(waitingJob))
}

class MySubscriber2(private val job: Job): FlowableSubscriber<Int> {
    override fun onSubscribe(s: Subscription) {
        s.request(200)
    }

    override fun onComplete() {
        println("onComplete()")
        job.cancel()
    }

    override fun onNext(t: Int?) {
        runBlocking { delay(100L) }
        println("OnNext(): $t - ${Thread.currentThread().name}")
    }

    override fun onError(t: Throwable) {
        println("onError: $t")
        job.cancel()
    }
}
```

```shell
// BackpressureStrategy.BUFFER는 무제한 버퍼를 제공합니다.
// 따라서 생산하는 데이터 개수가 많고, 수신하여 처리하는쪽이 너무 느릴경우 OutOfMemory가 발생할 수 있습니다.

send item 1
...
send item 10
OnNext(): 1 - RxComputationThreadPool-1
send item 11
...
send item 20
OnNext(): 2 - RxComputationThreadPool-1
send item 21
...
send item 30
OnNext(): 3 - RxComputationThreadPool-1
send item 31
.
.
.
send item 200
OnNext(): 20 - RxComputationThreadPool-1
OnNext(): 21 - RxComputationThreadPool-1
...
OnNext(): 200 - RxComputationThreadPool-1
```

```kotlin
runBlocking<Unit> {
    val flowable = Flowable.create({ view: FlowableEmitter<Int> ->
                                    for (i in 1..200) {
                                        println("send item $i")
                                        view.onNext(i)
                                        runBlocking { delay(10) }
                                    }
                                   }, BackpressureStrategy.ERROR)
    val waitingJob = launch { delay(1000) }
    flowable.observeOn(Schedulers.computation()).subscribe(ObservableJobs.Companion.MySubscriber3(waitingJob))
}

class MySubscriber2(private val job: Job): FlowableSubscriber<Int> {
    override fun onSubscribe(s: Subscription) {
        s.request(200)
    }

    override fun onComplete() {
        println("onComplete()")
        job.cancel()
    }

    override fun onNext(t: Int?) {
        runBlocking { delay(100L) }
        println("OnNext(): $t - ${Thread.currentThread().name}")
    }

    override fun onError(t: Throwable?): Unit = t!!.printStackTrace()
}
```

```shell
//기본 buffer 크기인 128개 까지는 데이터 방출이후에 처리가 따라가지 못하므로 onError()을 호출 하고 MissingBackpressureException을 발생시킵니다.
send item 1
...
send item 10
OnNext(): 1 - RxComputationThreadPool-1
send item 11
...
send item 20
OnNext(): 2 - RxComputationThreadPool-1
send item 21
.
.
.
send item 130
OnNext(): 13 - RxComputationThreadPool-1
err : io.reactivex.exceptions.MissingBackpressureException: create: could not emit value due to lack of requests
send item 131
...
send item 200
```

```kotlin
runBlocking<Unit> {
    val flowable = Flowable.create({ view: FlowableEmitter<Int> ->
                                    for (i in 1..200) {
                                        println("send item $i")
                                        view.onNext(i)
                                        runBlocking { delay(10) }
                                    }
                                   }, BackpressureStrategy.DROP)
    val waitingJob = launch { delay(1000) }
    flowable.observeOn(Schedulers.computation()).subscribe(ObservableJobs.Companion.MySubscriber3(waitingJob))
}

class MySubscriber2(private val job: Job): FlowableSubscriber<Int> {
    override fun onSubscribe(s: Subscription) {
        s.request(200)
    }

    override fun onComplete() {
        println("onComplete()")
        job.cancel()
    }

    override fun onNext(t: Int?) {
        runBlocking { delay(100L) }
        println("OnNext(): $t - ${Thread.currentThread().name}")
    }

    override fun onError(t: Throwable?): Unit = t!!.printStackTrace()
}
```

```shell
send item 1
...
send item 10
OnNext(): 1 - RxComputationThreadPool-1
send item 11
...
send item 20
OnNext(): 2 - RxComputationThreadPool-1
send item 21
.
.
.
send item 200
OnNext(): 20 - RxComputationThreadPool-1
OnNext(): 21 - RxComputationThreadPool-1
...
OnNext(): 128 - RxComputationThreadPool-1
```

```kotlin
runBlocking<Unit> {
    val flowable = Flowable.create({ view: FlowableEmitter<Int> ->
                                    for (i in 1..200) {
                                        println("send item $i")
                                        view.onNext(i)
                                        runBlocking { delay(10) }
                                    }
                                   }, BackpressureStrategy.LATEST)
    val waitingJob = launch { delay(1000) }
    flowable.observeOn(Schedulers.computation()).subscribe(ObservableJobs.Companion.MySubscriber3(waitingJob))
}

class MySubscriber2(private val job: Job): FlowableSubscriber<Int> {
    override fun onSubscribe(s: Subscription) {
        s.request(200)
    }

    override fun onComplete() {
        println("onComplete()")
        job.cancel()
    }

    override fun onNext(t: Int?) {
        runBlocking { delay(100L) }
        println("OnNext(): $t - ${Thread.currentThread().name}")
    }

    override fun onError(t: Throwable?): Unit = t!!.printStackTrace()
}
```

```shell
send item 1
...
send item 10
OnNext(): 1 - RxComputationThreadPool-1
send item 11
...
send item 20
OnNext(): 2 - RxComputationThreadPool-1
send item 21
.
.
.
send item 200
OnNext(): 20 - RxComputationThreadPool-1
OnNext(): 21 - RxComputationThreadPool-1
...
OnNext(): 128 - RxComputationThreadPool-1
OnNext(): 200 - RxComputationThreadPool-1
```

```kotlin
runBlocking<Unit> {
    val flowable = Flowable.create({ view: FlowableEmitter<Int> ->
                                    for (i in 1..200) {
                                        println("send item $i")
                                        view.onNext(i)
                                        runBlocking { delay(10) }
                                    }
                                   }, BackpressureStrategy.MISSING)
    val waitingJob = launch { delay(Long.MAX_VALUE) }
    flowable.onBackpressureBuffer(20).observeOn(Schedulers.computation()).subscribe(ObservableJobs.Companion.MySubscriber2(waitingJob))
}
```

<img src="https://t1.daumcdn.net/cfile/tistory/993CCB335DF1A78E0A" alt="onBackpressureBuffer" style="zoom:50%;" />

```shell
...
send item 44
send item 45
OnNext(): 5 - RxComputationThreadPool-1
onError: io.reactivex.exceptions.MissingBackpressureException: Buffer is full
send item 46
...
send item 200
```

```kotlin
runBlocking<Unit> {
    val flowable = Flowable.create({ view: FlowableEmitter<Int> ->
                                    for (i in 1..200) {
                                        println("send item $i")
                                        view.onNext(i)
                                        runBlocking { delay(10) }
                                    }
                                   }, BackpressureStrategy.MISSING)
    val waitingJob = launch { delay(Long.MAX_VALUE) }
    flowable.onBackpressureDrop{println("drop item $it")}.observeOn(Schedulers.computation()).subscribe(ObservableJobs.Companion.MySubscriber2(waitingJob))
}
```

<img src="https://t1.daumcdn.net/cfile/tistory/99A4A4335DF1A78E07" alt="onBackpressureDrop" style="zoom:50%;" />

```shell
OnNext(): 12 - RxComputationThreadPool-1
send item 128
send item 129
drop item 129
send item 130
drop item 130
...
send item 200
drop item 200
```

