import java.util.Arrays;

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
    float r = norm(V(O,S));
    float alpha = angle(V(O,S), V(O,E));
    length = alpha * r;
    
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
      if (i < num_of_circles) {
        pt C = P(S, (float) i/num_of_circles, E);
        centers[i] = C;
      } else
        centers[i] = E;
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
      vec OC = V(O, E);
      if (i < num_of_circles)
        OC = R(OS, i * d_alpha, OS_normalized, OS_normal_in_OSE);
      pt C = P(O, OC);
      centers[i] = C;
      OC.normalize();
      for (int j = 0; j < num_of_circle_vectors; j++) {
        vec K = R(K0, j * d_beta, K0_normalized, OC);
        circle_vectors[i][j] = K;
      }
      circle_vectors[i][num_of_circle_vectors] = circle_vectors[i][0];
    }
  }
  
  void twist_all(float t) { twist(t, t); }
  
  void twist_end(float t) { twist(0, t); }
  
  void twist(float head, float tail) {
    if (head != tail)
      isTwisted = true;
    for (int i = 0; i <= num_of_circles; i++) {
      vec bak_1 = circle_vectors[i][0];
      vec bak_2 = circle_vectors[i][num_of_circle_vectors / 4];
      vec bak_1_normalized = V(bak_1.x, bak_1.y, bak_1.z).normalize();
      vec bak_2_normalized = V(bak_2.x, bak_2.y, bak_2.z).normalize();
      for (int j = 0; j < num_of_circle_vectors; j++) {
        float difference = tail - head;
        circle_vectors_twisted[i][j] = R(circle_vectors[i][j], head + (float) i * difference / num_of_circles, bak_1_normalized, bak_2_normalized);
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

void drawTorusAroundStartCircle(Elbow e) {
  float rp = e.rc / 10;
  for (int i = 0; i < e.num_of_circle_vectors; i++) {
    fill(black);
    pt A = P(e.centers[0], e.circle_vectors[0][i]);
    pt B = P(e.centers[0], e.circle_vectors[0][i + 1]);
    cylinderSection(A, B, rp);
  }
}

class PPath {
  pt O;
  float rc;
  boolean isLine;
  int num_of_circles; int num_of_circle_vectors;
  pt[] centers;
  vec[] circle_vectors;
  vec[] circle_vectors_normal;
  
  PPath(Elbow e, int start_index) {
    this.O = e.O;
    this.rc = e.rc;
    this.isLine = e.isLine;
    this.num_of_circles = e.num_of_circles;
    this.num_of_circle_vectors = e.num_of_circle_vectors;
    this.centers = Arrays.copyOf(e.centers, e.centers.length);
    this.circle_vectors = new vec[num_of_circles + 1];
    this.circle_vectors_normal = new vec[num_of_circles + 1];
    for (int i = 0; i < e.centers.length; i++) {
      this.circle_vectors[i] = e.circle_vectors[i][start_index];
      this.circle_vectors_normal[i] = e.circle_vectors[i][start_index + num_of_circle_vectors / 4];
    }
  }
  
  PPath(Elbow e) {
    this.O = e.O;
    this.rc = e.rc;
    this.isLine = e.isLine;
    this.num_of_circles = e.num_of_circles;
    this.num_of_circle_vectors = e.num_of_circle_vectors;
    this.centers = Arrays.copyOf(e.centers, e.centers.length);
    this.circle_vectors = new vec[num_of_circles + 1];
    this.circle_vectors_normal = new vec[num_of_circles + 1];
    for (int i = 0; i < e.centers.length; i++) {
      this.circle_vectors[i] = e.circle_vectors[i][0];
      this.circle_vectors_normal[i] = e.circle_vectors[i][num_of_circle_vectors / 4];
    }
  }
  
  void twist_all(float t) { twist(t, t); }
  
  void twist_end(float t) { twist(0, t); }
  
  void twist(float head, float tail) {
    for (int i = 0; i <= num_of_circles; i++) {
      vec bak_1 = circle_vectors[i];
      vec bak_2 = circle_vectors_normal[i];
      vec bak_1_normalized = V(bak_1.x, bak_1.y, bak_1.z).normalize();
      vec bak_2_normalized = V(bak_2.x, bak_2.y, bak_2.z).normalize();
      float difference = tail - head;
      circle_vectors[i] = R(circle_vectors[i], head + (float) i * difference / num_of_circles, bak_1_normalized, bak_2_normalized);
    }
  }
}

void drawP(PPath ppath) {
  float rp = ppath.rc / 10;
  for (int i = 0; i < ppath.num_of_circles; i++) {
    fill(red);
    pt A = P(ppath.centers[i], ppath.circle_vectors[i]);
    pt B = P(ppath.centers[i + 1], ppath.circle_vectors[i + 1]);
    cylinderSection(A, B, rp);
  }
}
