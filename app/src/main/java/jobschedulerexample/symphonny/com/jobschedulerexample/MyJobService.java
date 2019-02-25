package jobschedulerexample.symphonny.com.jobschedulerexample;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class MyJobService extends JobService {
    private static final String TAG = MyJobService.class.getSimpleName();
    boolean isWorking = false;
    boolean jobCancelled = false;
    boolean needsReschedule = true;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started!");
        isWorking = true;
        // We need 'jobParameters' so we can call 'jobFinished'
        startWorkOnNewThread(params); // Services do NOT run on a separate thread
        return isWorking;
    }

    // Called if the job was cancelled before being finished
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before being completed.");
        jobCancelled = true;
        needsReschedule = isWorking;
        jobFinished(params,needsReschedule);
        return needsReschedule;
    }


    private void startWorkOnNewThread(final JobParameters jobParameters){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.d(TAG, "What Happened!!!!");
                doWork(jobParameters);
            }
        });
        thread.start();
    }

    private void doWork(JobParameters jobParameters){
        // 10 seconds of working (1000*10ms)
        for(int i = 0; i<1000; i++){
//            Log.d(TAG, "Running Loops");
            // If the job has been cancelled, stop working; the job will be rescheduled.
            if(jobCancelled){
                return;
            }
            try{
                Thread.sleep(10);
            }
            catch(Exception e){
                Log.d(TAG, e.toString());
            }
        }

        Log.d(TAG,"Job Finished");
        isWorking = false;
        needsReschedule = false;
        jobFinished(jobParameters,needsReschedule);
    }
}
