package model;

public class ModelBuilder {
    private float s;
    private float i;
    private float r;
    private float d;
    private final long PEOPLE_AMOUNT;
    private final float alpha;
    private final float beta;
    private final float mu;
    private final float DELTA;
    private final int TOTAL_DAYS = 365;


    public ModelBuilder(long n, float i, float alpha, float beta, float mu) {
        this.PEOPLE_AMOUNT = n;
        this.i = i;
        this.mu = mu;
        s = n - i;
        this.r = 0;
        this.alpha = alpha;
        this.beta = beta;
        // We don't actually care about dynamic delta
        // Plotter scales the data according to the windows size
        // Settle with 1 since 0.5 introduces jitter
        this.DELTA = 1;
    }

    private float evaluateDifS(float s, float i, float r, float alpha, float beta) {
        return -1 * beta * s * i / PEOPLE_AMOUNT;
    }

    private float evaluateDifI(float s, float i, float r, float alpha, float beta) {
        return beta * s * i / PEOPLE_AMOUNT - alpha * i;
    }

    private float evaluateDifR(float s, float i, float r, float alpha, float beta) {
        return alpha * i / PEOPLE_AMOUNT;
    }

    private float evaluateDifD(float r, float mu) {
        return mu * r;
    }

    private float evaluateFunction(float delta, float dif, float func) {
        return (delta * dif + func < 0 ? 0 : delta * dif + func);
    }

    private boolean equal(float r) {
        return  ((float) PEOPLE_AMOUNT - r < Math.log(Math.log(PEOPLE_AMOUNT)));
    }


    public PointsContainer build() {
        PointsContainer pointsContainer = new PointsContainer(TOTAL_DAYS);
        float x = 0;
        while (x < TOTAL_DAYS) {
            pointsContainer.addCoordinates(x, s, i, r, d);
            float difS = evaluateDifS(s, i, r, alpha, beta);
            float difI = evaluateDifI(s, i, r, alpha, beta);
            float difR = evaluateDifR(s, i, r, alpha, beta);
            float difD = evaluateDifD(r, mu);
            s = evaluateFunction(DELTA, difS, s);
            i = evaluateFunction(DELTA, difI, i);
            r = evaluateFunction(DELTA, difR, r);
            d = evaluateFunction(DELTA, difD, d);
            if (r >= PEOPLE_AMOUNT || d >= PEOPLE_AMOUNT) {
                pointsContainer.addCoordinates(x, 0, 0, PEOPLE_AMOUNT, d);
                break;
            }
            x += DELTA;
        }
        return pointsContainer;
    }
}
