package com.mineru.rxkotlinexam

import io.reactivex.FlowableSubscriber
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

class ObservableJobs {
    companion object {
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

        class MySubscriber2(private val job: Job): FlowableSubscriber<Int> {
            override fun onSubscribe(s: Subscription) {
                s.request(200)
            }

            override fun onComplete() {
                println("onComplete()")
                job.cancel()
            }

            override fun onNext(t: Int?) {
                runBlocking { delay(100) }
                println("OnNext(): $t - ${Thread.currentThread().name}")
            }

            override fun onError(t: Throwable) {
                println("onError: $t")
                job.cancel()
            }
        }

        class MySubscriber3: Subscriber<Int> {
            override fun onSubscribe(s: Subscription) {
                s.request(200)
            }

            override fun onComplete() {
                println("onComplete()")
            }

            override fun onNext(t: Int?) {
                runBlocking { delay(100) }
                println("OnNext(): $t - ${Thread.currentThread().name}")
            }
            override fun onError(e: Throwable) = e.printStackTrace()
        }
    }
}