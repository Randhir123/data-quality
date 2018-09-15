package com.bhge.dataquality;

import com.bhge.dataquality.domain.SensorReading;
import com.bhge.dataquality.util.SensorReadingProperties;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.SendTo;

import java.util.ArrayList;
import java.util.List;

@EnableConfigurationProperties(SensorReadingProperties.class)
@EnableBinding(Processor.class)
@SpringBootApplication
public class DataQualityApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataQualityApplication.class, args);
	}

	@Autowired
	private SensorReadingProperties properties;

	@StreamListener(Processor.INPUT)
	@SendTo(Processor.OUTPUT)
	public SensorReading SetValidity(SensorReading r) {

		Double validThreshold = properties.getValueThreshold();
		System.out.println("Valid Threshold:" + validThreshold.toString());
		System.out.println("Sensor reading:" + r.getValue());

		if (!isValid(r, String.valueOf(validThreshold))) {
			r.setInvalid(true);
		}

		return r;
	}

    private boolean isValid(SensorReading r, String threshold) {

	    Boolean valid = true;
	    //valid = Double.compare(Double.valueOf(r.getValue()), Double.valueOf(threshold)) > 0;
        String sensorReadingStr = r.toString();

/*
        String masterHost = System.getenv("KUBERNETES_SERVICE_HOST");
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://" + masterHost + ":2375")
                .build();

        DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory()
                .withReadTimeout(1000)
                .withConnectTimeout(1000)
                .withMaxTotalConnections(100)
                .withMaxPerRouteConnections(10);

        DockerClient dockerClient = DockerClientBuilder.getInstance(config)
                .withDockerCmdExecFactory(dockerCmdExecFactory)
                .build();
*/

        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        try {

            dockerClient
                    .pullImageCmd("randhirkumars/python-docker")
                    .withTag("latest")
                    .exec(new PullImageResultCallback() {
                        @Override
                        public void onNext(PullResponseItem item) {
                            super.onNext(item);
                            System.out.println(item.getStatus());
                        }
                    }).awaitCompletion();

            CreateContainerResponse createContainerResponse = dockerClient
                    .createContainerCmd("randhirkumars/python-docker")
                    .withEnv("RECORD=" + sensorReadingStr, "THRESHOLD=" + threshold)
                    .withBinds(new Bind("/var/run/docker.sock", new Volume("/var/run/docker.sock")))
                    .exec();

            dockerClient
                    .startContainerCmd(createContainerResponse.getId())
                    .exec();


            dockerClient
                    .waitContainerCmd(createContainerResponse.getId())
                    .exec(new WaitContainerResultCallback())
                    .awaitCompletion();

            final List<Frame> loggingFrames = getLoggingFrames(dockerClient, createContainerResponse.getId());

            for (final Frame frame : loggingFrames) {

                if (frame.toString().indexOf("INVALID") > 0) {
                    valid = false;
                }
            }
        } catch (Exception e) {
            valid = false;
        }

        return valid;
    }

    private List<Frame> getLoggingFrames(DockerClient dockerClient, String containerId) throws Exception {

        FrameReaderITestCallback collectFramesCallback = new FrameReaderITestCallback();

        dockerClient.logContainerCmd(containerId).withStdOut(true).withStdErr(true)
                .withTailAll()
                .exec(collectFramesCallback).awaitCompletion();

        return collectFramesCallback.frames;
    }

    public static class FrameReaderITestCallback extends LogContainerResultCallback {

        public List<Frame> frames = new ArrayList<>();

        @Override
        public void onNext(Frame item) {
            frames.add(item);
            super.onNext(item);
        }

    }
}
