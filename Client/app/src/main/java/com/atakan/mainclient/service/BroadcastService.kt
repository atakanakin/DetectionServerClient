package com.atakan.mainclient.service

/*

@AndroidEntryPoint
class BroadcastService : Service() {
    @Inject
    lateinit var getCurrencyUseCase: GetCurrencyUseCase
    // Get viewModel instance
    @Inject
    lateinit var viewModel: CurrencyViewModel

    @Inject
    lateinit var clickViewModel: ServiceViewModel

    var sendMessage: Boolean = false

    private val clickObserver = Observer<Boolean> {
        sendMessage = it
        sendDataToServer()
    }

    val apiHandler = Handler()
    private val apiRunnable = object : Runnable {
        override fun run() {

            // Make the API call and update viewModel with the response data
            Log.d("Broadcast", "Fetching data from API")

            val job = CoroutineScope(Dispatchers.IO).launch {
                getCurrencyUseCase.invoke().collect{
                    when (it) {
                        is Resource.Success -> {
                            // Update viewModel with the fetched resource
                            viewModel.refreshData(it)
                        }
                        is Resource.Loading -> {
                            //
                        }
                        is Resource.Error -> {
                            // Handle error state if needed
                            println("Error: ${it.message}")
                        }
                    }
                }

            }

            // Make sure to cancel the coroutine when the service is stopped
            job.invokeOnCompletion {
                if (it != null) {
                    Log.e("Broadcast Error","Coroutine completed with an exception: ${it.message}")
                } else {
                    //
                }
            }

            if(sendMessage){
                sendDataToServer()
            }

            // Repeat this process every minute
            apiHandler.postDelayed(this, 5000)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Start the periodic API calls and send data if the service is bounded
        apiHandler.post(apiRunnable)

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.d("Broadcast", "Task Removed, Restarting Service")
        apiHandler.removeCallbacks(apiRunnable)
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    override fun onCreate() {
        super.onCreate()
        // Create a notification channel for the foreground service (for Android Oreo and above)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Foreground Service Channel"
            val descriptionText = "Foreground service channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("ForegroundServiceChannel", name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification for the foreground service
        val notification: Notification = createNotification()

        // Start the service as a foreground service with the notification
        startForeground(3, notification)
        clickViewModel.isServiceConnected.observeForever(clickObserver)
    }

    // Create the notification for the foreground service
    private fun createNotification(): Notification {
        val notificationTitle = "Broadcast Service"
        val notificationText = "Service is running in the background"
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                // Add the extra value for fragment identification
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "ForegroundServiceChannel")
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentIntent(pendingIntent)
            .build()
    }
    override fun onDestroy() {
        super.onDestroy()
        apiHandler.removeCallbacks(apiRunnable)
        clickViewModel.isServiceConnected.removeObserver(clickObserver)
        stopForeground(true)
    }

    private fun sendDataToServer(){
        val intent = Intent()

        when (val resource: Resource<Currency>? = viewModel.currencyLiveData.value) {
            is Resource.Success ->{
                val currencyData: Currency = resource.data!!
                Log.d("Broadcast", "Sending Data")
                //Send Broadcast
                intent.action = "com.atakan.mainclient"
                intent.putExtra(PACKAGE_NAME, applicationContext?.packageName)
                intent.putExtra(PID, Process.myPid().toString())
                //intent.putExtra(DATA, binding.edtClientData.text.toString())
                intent.putExtra(CURR1, currencyData.bpi.USD.code)
                intent.putExtra(CURR2, currencyData.bpi.EUR.code)
                intent.putExtra(CURR3, currencyData.bpi.GBP.code)

                intent.putExtra(RATE1, currencyData.bpi.USD.rate_float.toDouble()!!)
                intent.putExtra(RATE2, currencyData.bpi.EUR.rate_float.toDouble()!!)
                intent.putExtra(RATE3, currencyData.bpi.GBP.rate_float.toDouble()!!)
                intent.putExtra(TIME, currencyData.time.updated)
                intent.component = ComponentName("com.atakan.mainserver", "com.atakan.mainserver.service.BroadcastReceiver")
                sendBroadcast(intent)
            }
            is Resource.Loading -> {
                Log.d("Broadcast", "Loading")
            }

            is Resource.Error -> {
                Log.e("Broadcast", "Unexpected error occurred.")
            }
            else -> {
                Log.w("Broadcast", "Null")
            }
        }
    }
}

 */