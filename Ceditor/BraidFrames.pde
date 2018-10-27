class ElbowBraidFrames {
  pt[] centers;
  vec[] zeroVectors;
  vec[] zeroVectorNormals;
  float[] timeOfEachFrame;
  pt[][] pointsInEachFrame;
  int numOfBraids;
  float rc;

  ElbowBraidFrames(Elbow e, float lengthBeforeThisElbow, float totalLength, int numOfBraids) {
    this.centers = e.centers;
    this.numOfBraids = numOfBraids;
    this.rc = e.rc;
    zeroVectors = new vec[e.centers.length];
    zeroVectorNormals = new vec[e.centers.length];
    timeOfEachFrame = new float[e.centers.length];
    pointsInEachFrame = new pt[e.centers.length][numOfBraids];
    for (int i = 0; i < e.centers.length; i++) {
      zeroVectors[i] = V(e.circle_vectors[i][0]).normalize();
      zeroVectorNormals[i] = V(e.circle_vectors[i][e.num_of_circle_vectors / 4]).normalize();
      timeOfEachFrame[i] = (lengthBeforeThisElbow + e.arcLength * i / e.num_of_circles) / totalLength;
    }
  }

  void setCood(int indexOfFrame, float[] coodInFrame, int direction) {
    for (int i = 0; i < numOfBraids; i++) {
      vec bak_1 = zeroVectors[indexOfFrame];
      vec bak_2 = zeroVectorNormals[indexOfFrame];
      vec vectorFromCenterToPoint = V(coodInFrame[2 * i] * rc, R(bak_1, coodInFrame[2 * i + 1] * direction, bak_1, bak_2));
      pointsInEachFrame[indexOfFrame][i] = P(centers[indexOfFrame], vectorFromCenterToPoint);
    }
  }

  void draw(float rb) {
    for (int i = 0; i < centers.length - 1; i++) {
      for (int j = 0; j < numOfBraids; j++) {
        fill(red);
        cylinderSection(pointsInEachFrame[i][j], pointsInEachFrame[i + 1][j], rb * rc);
      }
    }
  }
}

class CurveBraidFrames {
  ElbowBraidFrames[] elbowOfFrames;
  float totalLength;
  float[] lengthBeforeEachElbow;
  int methodId;
  int numOfBraids;
  multiPointsMotion2D MPM;
  int numOfPeriods;

  // for each elbow in CurveElbow create ElbowBraidFrames
  // for each ElbowBraidFrames
  //     for each Frame
  //         calculate real time for it
  //         get coods for all points inside
  //         set coods for this Frame
  CurveBraidFrames(CurveElbow ce, multiPointsMotion2D MPM, int methodId, int numOfPeriods) {
    getLengths(ce);
    this.MPM = MPM;
    this.methodId = methodId;
    numOfBraids = MPM.methodNumPoints(methodId);
    this.numOfPeriods = numOfPeriods;
    setUpElbowBraidFrames(ce);
  }

  void setUpElbowBraidFrames(CurveElbow ce) {
    elbowOfFrames = new ElbowBraidFrames[ce.elbows.length];
    for (int i = 0; i < ce.elbows.length; i++) {
      ElbowBraidFrames ebf = new ElbowBraidFrames(ce.elbows[i], lengthBeforeEachElbow[i], totalLength, numOfBraids);
      elbowOfFrames[i] = ebf;
      int direction = 1;
      for (int j = 0; j < ebf.centers.length; j++) {
        float realTime = getRealTime(ebf.timeOfEachFrame[j], numOfPeriods);
        this.MPM.set(realTime, methodId);
        if (i > 0 && j == 0) {
          ebf.setCood(j, MPM.cood, direction);
          ElbowBraidFrames last_ebf = elbowOfFrames[i - 1];
          pt last_ebf_0 = last_ebf.pointsInEachFrame[last_ebf.centers.length - 1][0];
          pt ebf_0 = ebf.pointsInEachFrame[0][0];
          System.out.println(last_ebf.timeOfEachFrame[last_ebf.centers.length - 1]);
          System.out.println(ebf.timeOfEachFrame[0]);
          System.out.println("________");
          float d = norm(V(last_ebf_0, ebf_0));
          if (d > 0.001) direction = 1;
        }
        ebf.setCood(j, MPM.cood, direction);
      }
    }
  }

  void draw() {
    for (int i = 0; i < elbowOfFrames.length; i++)
      elbowOfFrames[i].draw(MPM.radius);
  }

  void getLengths(CurveElbow ce) {
    totalLength = 0;
    lengthBeforeEachElbow = new float[ce.elbows.length];
    for (int i = 0; i < ce.elbows.length; i++) {
      totalLength += ce.elbows[i].arcLength;
      if (i != 0) lengthBeforeEachElbow[i] = lengthBeforeEachElbow[i - 1] + ce.elbows[i].arcLength;
    }
  }

  float getRealTime(float timeOfFrame, int numOfPeriods) {
    float periodTime = 1.0 / numOfPeriods;
    while (timeOfFrame > periodTime)
      timeOfFrame -= periodTime;
    return timeOfFrame / periodTime;
  }
}
