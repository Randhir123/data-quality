# data-quality
To build Docker image, make sure you are logged into Docker daemon, and run the following command

mvn compile jib:dockerBuild -Djib.to.auth.username=your-docker-username -Djib.to.auth.password=your-docker-password
