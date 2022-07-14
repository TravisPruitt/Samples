# Reference code and samples

This project only contains code and project artifact samples.

![Command Sight Architecture](./Architectures/Command%20Sight%20Architecture.jpeg)

## Code References

Here are some code samples that most relates to the identified area. Unless specifically noted, I have authored most of the artifacts and/or code identified.

### Hardware/IoT

### Disney MagicBand

**Disney xConnect** <https://youtu.be/lmSV1Mz7rgw>
**Disney MagicBand** <https://youtu.be/TkP8nl8e4xY>

#### xReader

C++ code that implements the controller for the hardware that reads all band communications within its range.

./Code/Disney/disney.reader/xBR/reader

#### xController Source

Java code that implements the bus to receive all band messages, determines guest locations based on "strength of signal" algorithms.

./Code/Disney/disney.controller/xBR/controller

#### Microsoft Connected Campus

<https://youtu.be/zurGw9kRz1Q>

### Mobile Devices

#### Netsuite

<https://youtu.be/Vjixz6rtJSE>

Overhauled NetSuite's mobile strategy, branding, and development practices. This includes designing and developing new Single Purpose Mobile applications and new services to surface "just in time" content.

*Vision and strategy*
<https://tinyurl.com/37r6xn3y>

#### Cliniqly

<https://vimeo.com/97388385>

Proof of concept project that utilized a patient's phone to book doctor's appointments, maintain insurance information, and utilizes in-clinic hardware to track a patient's journey through-out their visit. The proof of concept was used in multiple Swedish Hospital, pop-up clinics.

#### Penchant

<https://youtu.be/4NFogF66Kns>

Early mobile application concept developed in Flutter that utilized voice and sentiment analysis to identify trending podcast content. This was later utilized by iHeartRadio to enhance their podcasting platform.

./Code/Mobile/Penchant

### Dev/ML Ops

#### Google Cloud Hero

<https://youtu.be/KOfbWR-eaHI>

Conceived and implemented the Google Cloud Hero program as a Google partner. Program was run world-wide to help introduce engineers to new cloud concepts and best practices. As an experience that engineers would participate in, we would monitor each individual's progress, help them understand specific cloud technologies. As their partner, we would work directly with enterprises on their challenging scenarios, scale and cost issues, and up-lifting internal resources. Much of the emerging needs were in the machine learning and IoT space.

The platform was developed using Angular with virtual development environments scaled to thousands of simultaneous users utilizing Kubernetes clusters.

#### Clobotics Wind Services

<https://clobotics.com>

Re-architected and modernized the Clobotics Wind Services platform to utilize Google real-time video analytics. Additional pipelines were created to coordinate and manage automated drone fleets.  

Much of this work came from some of my early research. 360 footage is captured from a drone, utilized ML to upscale and remove "prop wash" jitters. The live video is routed through cloud-based services to identify Points of Interest (object recognition, text translation, face recognition, gesture recognition) and "reframe" a single 360 video source into "labelled" sub-clips. These clips can then be orchestrated into a single live feed showing only "highlights".

This is an early demo and ML explainer video that tries to keep it very simple for the client.

<https://youtu.be/0TChGgiPb3I?t=1543>

#### Model Training

Here is an example Jupyter notebook that is used build a model for inferring sidewalk detection. This was a key model used to route automated delivery robots.

![Juptyer Notebook]()

This is a kubeflow pipeline that builds a simple predictive model on some financial data. Used in concert with other pipelines to detect financial anomalies and fraud.

![Kubeflow Pipeline](./code/ml/kubeflow/finpredict.kfp)
