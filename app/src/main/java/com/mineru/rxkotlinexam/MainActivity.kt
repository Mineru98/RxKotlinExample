package com.mineru.rxkotlinexam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.AsyncSubject
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1.setOnClickListener {
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
        }

        btn2.setOnClickListener {
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
        }

        btn3.setOnClickListener {
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
        }

        btn4.setOnClickListener {
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
        }

        btn5.setOnClickListener {
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
        }

        btn6.setOnClickListener {
            val observer = Observable.range(1,3)

            observer.subscribe(
                { item -> println("onNext - $item")},
                { err -> println("onError - $err")},
                { println("onComplete()")},
                { d -> println("onSubscribe() - $d")}
            )
        }

        btn7.setOnClickListener {
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
        }

        btn8.setOnClickListener {
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
        }

        btn9.setOnClickListener {
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
        }

        btn10.setOnClickListener {
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
                delay(3000L)
            }
        }

        btn11.setOnClickListener {
            val observable = Observable.interval(1000L, TimeUnit.MILLISECONDS)
            val subject = PublishSubject.create<Long>()
            observable.subscribe(subject)
            runBlocking { delay(3000L) }
            subject.subscribe { println("1st: $it") }
            runBlocking { delay(3000L) }
            subject.subscribe { println("2nd: $it") }
            runBlocking { delay(3000L) }
        }

        btn12.setOnClickListener {
            val observable = Observable.interval(1000L, TimeUnit.MILLISECONDS)
            val subject = BehaviorSubject.create<Long>()
            observable.subscribe(subject)
            runBlocking { delay(3000L) }
            subject.subscribe { println("1st: $it") }
            runBlocking { delay(3000L) }
            subject.subscribe { println("2nd: $it") }
            runBlocking { delay(3000L) }
        }

        btn13.setOnClickListener {
            val observable = Observable.just(1,2,3,4,5,6,7,8,9,10)
            val subject = AsyncSubject.create<Int>()
            observable.subscribe(subject)
            subject.subscribe { println("1st: $it") }
            subject.subscribe { println("2nd: $it") }
        }

        btn14.setOnClickListener {
            val observable = Observable.interval(1000L, TimeUnit.MILLISECONDS)
            val subject = ReplaySubject.create<Long>()
            observable.subscribe(subject)
            runBlocking { delay(3000L) }
            subject.subscribe { println("1st: $it") }
            runBlocking { delay(3000L) }
            subject.subscribe { println("2nd: $it") }
            runBlocking { delay(3000L) }
        }

        btn15.setOnClickListener {
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
        }

        btn16.setOnClickListener {
            runBlocking<Unit> {
                val observable = Observable.range(1, 10)
                observable
                    .map {
                        val str = "Mapping item $it"
                        runBlocking { delay(1000L) }
                        println("$str - ${Thread.currentThread().name}")
                        str
                    }
                    .observeOn(Schedulers.computation())
                    .subscribe {
                         runBlocking{ delay(2000L) }
                        println("Received $it - ${Thread.currentThread().name}")
                    }
                delay(1000L)
            }
        }

        btn17.setOnClickListener {
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
        }

        btn18.setOnClickListener {
            runBlocking<Unit>{
                Flowable.range(1,150).map {
                    println("Mapping item $it - ${Thread.currentThread().name}")
                    it
                }.observeOn(Schedulers.computation())
                    .subscribe(ObservableJobs.Companion.MySubscriber())
                delay(5000L)
            }
        }

        btn19.setOnClickListener {
            runBlocking<Unit> {
                val flowable = Flowable.create({ view: FlowableEmitter<Int> ->
                    for (i in 1..200) {
                        println("send item $i")
                        view.onNext(i)
                        runBlocking { delay(10) }
                    }
                }, BackpressureStrategy.BUFFER)
                val waitingJob = launch { delay(Long.MAX_VALUE) }
                flowable.observeOn(Schedulers.computation()).subscribe(ObservableJobs.Companion.MySubscriber2(waitingJob))
            }
        }

        btn20.setOnClickListener {
            runBlocking<Unit> {
                val flowable = Flowable.create({ view: FlowableEmitter<Int> ->
                    for (i in 1..200) {
                        println("send item $i")
                        view.onNext(i)
                        runBlocking { delay(10) }
                    }
                }, BackpressureStrategy.ERROR)

                flowable.observeOn(Schedulers.computation()).subscribe(ObservableJobs.Companion.MySubscriber3())
                delay(1000)
            }
        }

        btn21.setOnClickListener {
            runBlocking<Unit> {
                val flowable = Flowable.create({ view: FlowableEmitter<Int> ->
                    for (i in 1..200) {
                        println("send item $i")
                        view.onNext(i)
                        runBlocking { delay(10) }
                    }
                }, BackpressureStrategy.DROP)

                flowable.observeOn(Schedulers.computation()).subscribe(ObservableJobs.Companion.MySubscriber3())
                delay(1000)
            }
        }

        btn22.setOnClickListener {
            runBlocking<Unit> {
                val flowable = Flowable.create({ view: FlowableEmitter<Int> ->
                    for (i in 1..200) {
                        println("send item $i")
                        view.onNext(i)
                        runBlocking { delay(10) }
                    }
                }, BackpressureStrategy.LATEST)

                flowable.observeOn(Schedulers.computation()).subscribe(ObservableJobs.Companion.MySubscriber3())
                delay(1000)
            }
        }

        btn23.setOnClickListener {
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
        }

        btn24.setOnClickListener {
            runBlocking<Unit> {
                val flowable = Flowable.create({ view: FlowableEmitter<Int> ->
                    for (i in 1..200) {
                        println("send item $i")
                        view.onNext(i)
                        runBlocking { delay(10) }
                    }
                }, BackpressureStrategy.MISSING)
                val waitingJob = launch { delay(Long.MAX_VALUE) }
//                flowable.onBackpressureBuffer(20)
                flowable.onBackpressureDrop{println("Drop item $it")}.observeOn(Schedulers.computation()).subscribe(ObservableJobs.Companion.MySubscriber2(waitingJob))
            }
        }
    }
}
