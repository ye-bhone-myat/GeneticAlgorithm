import Machine.Shapes;

class Result implements Cloneable{
    private int[] xVals;
    private int[] yVals;
    private Shapes[] shapes;
    private int score, generation;
    private long duration;
    private int tileCount;

    Result(int[] xVals, int[] yVals, Shapes[] shapes, int score, int generation){
        this.xVals = xVals;
        this.yVals = yVals;
        this.shapes = shapes;
        this.score = score;
//        this.duration = duration;
        this.generation = generation;
        this.tileCount = xVals.length;
    }

    public void setDuration(long duration){
        this.duration = duration;
    }

    public int getGeneration(){
        return generation;
    }

    public int[] getxVals(){
        return xVals;
    }

    public int[] getyVals(){
        return yVals;
    }

    public int getTileCount() {
        return tileCount;
    }

    public Shapes[] getShapes() {
        return shapes;
    }

    public int getScore(){
        return score;
    }

    public long getDuration(){
        return duration;
    }

    @Override
    public Result clone(){
        try {
            Result r = (Result) super.clone();
            r.xVals = xVals.clone();
            r.yVals = yVals.clone();
            r.shapes = shapes.clone();
            return r;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

}