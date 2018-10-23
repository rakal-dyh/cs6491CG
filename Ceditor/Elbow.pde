class Elbow {
  pt S; pt E; pt O;
  float rc;
  boolean isLine;
  int num_of_circles; int num_of_circle_vectors;
  pt[] centers;
  vec[][] circle_vectors;
  boolean isTwisted;
  vec[][] circle_vectors_twisted;
  float length;
  color[] cls = new color[]{yellow, blue, orange, green};

  Elbow(pt S, pt E, pt O, boolean isLine) {
    this.S = S; this.E = E; this.O = O;
    this.rc = 20;
    this.isLine = isLine;
    this.num_of_circles = 36;
    this.num_of_circle_vectors = 36;
    this.centers = new pt[num_of_circles + 1];
    this.circle_vectors = new vec[num_of_circles + 1][num_of_circle_vectors + 1];
    this.circle_vectors_twisted = new vec[num_of_circles + 1][num_of_circle_vectors + 1];

    if (this.isLine) calculateFieldsLine();
    else calculateFields();
  }

  void calculateFieldsLine() {
    vec SE = V(S, E);
    vec K0 = Normal(SE);
    K0 = V(rc, K0.normalize());
    vec K0_normalized = V(K0.x, K0.y, K0.z);
    K0_normalized.normalize();
    vec OC = N(SE, K0);
    OC.normalize();

    float d_beta = TWO_PI / num_of_circle_vectors;

    // for loop limit: alpha < e * TWO_PI - d_alpha / 2
    for (int i = 0; i <= num_of_circles; i++) {
      pt C = P(S, (float) i/num_of_circles, E);
      centers[i] = C;
      for (int j = 0; j < num_of_circle_vectors; j++) {
        vec K = R(K0, j * d_beta, K0_normalized, OC);
        circle_vectors[i][j] = K;
      }
      circle_vectors[i][num_of_circle_vectors] = circle_vectors[i][0];
    }
  }

  void calculateFields() {
    vec OS = V(O, S);
    vec OS_normalized = V(OS.x, OS.y, OS.z);
    OS_normalized.normalize();
    vec OE = V(O, E);
    vec K0 = N(OS, OE);
    K0 = V(20, K0.normalize());
    vec K0_normalized = V(K0.x, K0.y, K0.z);
    K0_normalized.normalize();
    vec OS_normal_in_OSE = B(OS, OE); // in plane O, S, E and orthogonal to OS
    OS_normal_in_OSE.normalize();

    float alpha = angle(OS, OE);
    float d_alpha = alpha / num_of_circles;
    float d_beta = TWO_PI / num_of_circle_vectors;

    // for loop limit: alpha < e * TWO_PI - d_alpha / 2
    for (int i = 0; i <= num_of_circles; i++) {
      vec OC = R(OS, i * d_alpha, OS_normalized, OS_normal_in_OSE);
      pt C = P(O, OC);
      centers[i] = C;
      if (i == 8) System.out.println("C: " + C.x + " " + C.y + " " + C.z);
      OC.normalize();
      for (int j = 0; j < num_of_circle_vectors; j++) {
        vec K = R(K0, j * d_beta, K0_normalized, OC);
        circle_vectors[i][j] = K;
      }
      circle_vectors[i][num_of_circle_vectors] = circle_vectors[i][0];
    }
  }

  void twist_all(float t) {
    for (int i = 0; i <= num_of_circles; i++) {
      vec bak_1 = circle_vectors[i][0];
      vec bak_2 = circle_vectors[i][num_of_circle_vectors / 4];
      vec bak_1_normalized = V(bak_1.x, bak_1.y, bak_1.z).normalize();
      vec bak_2_normalized = V(bak_2.x, bak_2.y, bak_2.z).normalize();
      for (int j = 0; j < num_of_circle_vectors; j++) {
        circle_vectors[i][j] = R(circle_vectors[i][j], t, bak_1_normalized, bak_2_normalized);
      }
      circle_vectors[i][num_of_circle_vectors] = circle_vectors[i][0];
    }
  }

  void twist_end(float t) {
    isTwisted = true;
    for (int i = 0; i <= num_of_circles; i++) {
      vec bak_1 = circle_vectors[i][0];
      vec bak_2 = circle_vectors[i][num_of_circle_vectors / 4];
      vec bak_1_normalized = V(bak_1.x, bak_1.y, bak_1.z).normalize();
      vec bak_2_normalized = V(bak_2.x, bak_2.y, bak_2.z).normalize();
      for (int j = 0; j < num_of_circle_vectors; j++) {
        circle_vectors_twisted[i][j] = R(circle_vectors[i][j], (float) i * t / num_of_circles, bak_1_normalized, bak_2_normalized);
      }
      circle_vectors_twisted[i][num_of_circle_vectors] = circle_vectors_twisted[i][0];
    }
  }
}

void drawElbow(Elbow e) {
  for (int i = 0; i < 4; i++) {
    fill(e.cls[i]);
    for (int j = 0; j < e.num_of_circles; j++) {
      int num_of_vectors_in_strip = e.num_of_circle_vectors / 4;
      int start_index = i * num_of_vectors_in_strip;
      vec[][] circle_vectors = e.circle_vectors_twisted;
      if (!e.isTwisted) circle_vectors = e.circle_vectors;
      for (int k = start_index; k < start_index + num_of_vectors_in_strip; k++) {
        pt A = P(e.centers[j], circle_vectors[j][k]);
        pt B = P(e.centers[j], circle_vectors[j][k + 1]);
        pt C = P(e.centers[j + 1], circle_vectors[j + 1][k + 1]);
        pt D = P(e.centers[j + 1], circle_vectors[j + 1][k]);
        show(A, B, C, D);
      }
    }
  }
}

class PPath {

}

void drawP(Elbow e, int index) {
  float rp = e.rc / 10;
  for (int i = 0; i < e.num_of_circles; i++) {
    fill(red);
    pt A = P(e.centers[i], e.circle_vectors[i][index]);
    pt B = P(e.centers[i + 1], e.circle_vectors[i + 1][index]);
    cylinderSection(A, B, rp);
  }
}
