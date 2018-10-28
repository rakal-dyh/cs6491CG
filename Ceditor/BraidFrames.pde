class ElbowBraidFrames {
  pt[] centers;
  vec[] zeroVectors;
  vec[] zeroVectorNormals;
  float[] timeOfEachFrame;
  pt[][] pointsInEachFrame;
  float[][] coodsInEachFrame;
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
    coodsInEachFrame = new float[e.centers.length][numOfBraids];
    for (int i = 0; i < e.centers.length; i++) {
      zeroVectors[i] = V(e.circle_vectors_twisted[i][0]).normalize();
      zeroVectorNormals[i] = V(e.circle_vectors_twisted[i][e.num_of_circle_vectors / 4]).normalize();
      timeOfEachFrame[i] = (lengthBeforeThisElbow + e.arcLength * i / e.num_of_circles) / totalLength;
    }
  }

  void setCood(int indexOfFrame, float[] coodInFrame, float direction) {
    for (int i = 0; i < numOfBraids; i++) {
      vec bak_1 = zeroVectors[indexOfFrame];
      vec bak_2 = zeroVectorNormals[indexOfFrame];
      vec vectorFromCenterToPoint = V(coodInFrame[2 * i] * rc, R(bak_1, direction * coodInFrame[2 * i + 1], bak_1, bak_2));
      pointsInEachFrame[indexOfFrame][i] = P(centers[indexOfFrame], vectorFromCenterToPoint);
    }
    //System.out.println(direction + ": angle " + coodInFrame[1] * direction + "cood " + pointsInEachFrame[indexOfFrame][0].x);
  }
  
  void setCood(int indexOfFrame, float[] coodInFrame, float offset, float direction) {
    coodsInEachFrame[indexOfFrame] = Arrays.copyOf(coodInFrame, coodInFrame.length);
    for (int i = 0; i < numOfBraids; i++) {
      vec bak_1 = zeroVectors[indexOfFrame];
      vec bak_2 = zeroVectorNormals[indexOfFrame];
      vec vectorFromCenterToPoint = V(coodInFrame[2 * i] * rc, R(bak_1, direction * coodInFrame[2 * i + 1] + offset, bak_1, bak_2));
      pointsInEachFrame[indexOfFrame][i] = P(centers[indexOfFrame], vectorFromCenterToPoint);
    }
    //System.out.println(direction + ": angle " + coodInFrame[1] * direction + "cood " + pointsInEachFrame[indexOfFrame][0].x);
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

      float direction = 1;
      for (int j = 0; j < ebf.centers.length; j++) {
        float realTime = getRealTime(ebf.timeOfEachFrame[j], numOfPeriods);
        this.MPM.set(realTime, methodId);
        if (i > 0 && j == 0) {
          ebf.setCood(j, MPM.cood, direction);
          ElbowBraidFrames last_ebf = elbowOfFrames[i - 1];
          /*
          System.out.println("last: " + last_ebf.timeOfEachFrame[last_ebf.timeOfEachFrame.length - 1]);
          pt last_center = last_ebf.centers[last_ebf.centers.length - 1];
          System.out.println(last_center.x + " " + last_center.y + " " + last_center.z);
          System.out.println(Arrays.toString(last_ebf.coodsInEachFrame[last_ebf.coodsInEachFrame.length - 1]));

          System.out.println("this: " + ebf.timeOfEachFrame[0]);
          pt this_center = ebf.centers[0];
          System.out.println(this_center.x + " " + this_center.y + " " + this_center.z);
          System.out.println(Arrays.toString(MPM.cood));
          
          //System.out.println("________");
          */
          pt last_ebf_0 = last_ebf.pointsInEachFrame[last_ebf.centers.length - 1][0];
          pt ebf_0 = ebf.pointsInEachFrame[0][0];
          
          //vec last_ebf_0v = V(last_ebf.centers[last_ebf.centers.length - 1], last_ebf_0);
          
          float d = norm(V(last_ebf_0, ebf_0));
          if (d > 0.001) {
            //System.out.println(last_ebf_0.x + " " + last_ebf_0.y + " " + last_ebf_0.z);
            //System.out.println(offset + ": " + ebf_0.x + " " + ebf_0.y + " " + ebf_0.z);
            //ebf_0 = ebf.pointsInEachFrame[0][0];
            //vec ebf_0v = V(ebf.centers[0], ebf_0);
            //offset = angle(last_ebf_0v, ebf_0v);
            
            /*
            ebf.setCood(j, MPM.cood, offset, direction);
            ebf_0 = ebf.pointsInEachFrame[0][0];
            d = norm(V(last_ebf_0, ebf_0));
            if (d < 0.001) {
              break;
            }
            
            ebf.setCood(j, MPM.cood, offset, -direction);
            ebf_0 = ebf.pointsInEachFrame[0][0];
            d = norm(V(last_ebf_0, ebf_0));
            if (d < 0.001) {
              direction = -direction;
              break;
            }
            
            ebf.setCood(j, MPM.cood, -offset, direction);
            ebf_0 = ebf.pointsInEachFrame[0][0];
            d = norm(V(last_ebf_0, ebf_0));
            if (d < 0.001) {
              offset = -offset;
              break;
            }
            
            ebf.setCood(j, MPM.cood, -offset, -direction);
            ebf_0 = ebf.pointsInEachFrame[0][0];
            d = norm(V(last_ebf_0, ebf_0));
            if (d < 0.001) {
              offset = -offset;
              direction = -direction;
              break;
            }
            */
            direction = -1;
            //ebf_0 = ebf.pointsInEachFrame[0][0];
            //d = norm(V(last_ebf_0, ebf_0));
            //System.out.println(offset + ": " + ebf_0.x + " " + ebf_0.y + " " + ebf_0.z);
            //System.out.println("________");
          }
        }
        ebf.setCood(j, MPM.cood, direction);
        //if (i == 0 && j == 0) System.out.println(Arrays.toString(MPM.cood));
      }
    }
  }

  void draw(String type) {
    if (type.equals("cylinder")) {
      for (int i = 0; i < elbowOfFrames.length; i++)
        elbowOfFrames[i].draw(MPM.radius);
    } else if (type.equals("PCC")) {
      for (int i = 0; i < numOfBraids; i++) {
        pts braidPts = new pts();
        braidPts.declare();
        for (int j = 0; j < elbowOfFrames.length; j++) {
          for (int k = 0; k < elbowOfFrames[j].pointsInEachFrame.length - 1; k++) {
            braidPts.addPt(elbowOfFrames[j].pointsInEachFrame[k][i]);
            //if (j == 0 && k == 0) System.out.println(elbowOfFrames[j].pointsInEachFrame[k][i]);
          }
        }
        //System.out.println(braidPts);
        elbowControl braidPCC = new elbowControl(braidPts, 2, 4, MPM.radius * elbowOfFrames[0].rc);
        braidPCC.curvebow.draw();
      }
    } else if (type.equals("closedCurve")) {
      for (int i = 0; i < numOfBraids; i++) {
        pts braidPts = new pts();
        braidPts.declare();
        for (int j = 0; j < elbowOfFrames.length; j++) {
          for (int k = 0; k < elbowOfFrames[j].pointsInEachFrame.length - 1; k++) {
            braidPts.addPt(elbowOfFrames[j].pointsInEachFrame[k][i]);
          }
        }
        braidPts.drawClosedCurve(MPM.radius * elbowOfFrames[0].rc / 3);
      }
    }
  }

  void getLengths(CurveElbow ce) {
    totalLength = 0;
    lengthBeforeEachElbow = new float[ce.elbows.length];
    for (int i = 0; i < ce.elbows.length; i++) {
      lengthBeforeEachElbow[i] = totalLength;
      totalLength += ce.elbows[i].arcLength;
    }
  }

  float getRealTime(float timeOfFrame, int numOfPeriods) {
    float onePeriod = 1.0 / numOfPeriods;
    while (timeOfFrame > onePeriod) timeOfFrame -= onePeriod;
    return timeOfFrame / onePeriod;
  }
}
