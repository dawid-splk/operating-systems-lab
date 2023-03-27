package Zad4;

public class Page {

    private Process process;
    private int pageNumber;
    private int util;

    public Page(Process process, int pageNumber){
        this.process = process;
        this.pageNumber = pageNumber + 1;
        util = 0;
    }

    public Page(Page p){
        this.pageNumber = p.pageNumber;
        util = 0;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getUtil() {
        return util;
    }

    public void setUtil(int util) {
        this.util = util;
    }

    public Process getProcess() {
        return process;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return pageNumber == page.pageNumber;
    }

    @Override
    public String toString(){
        return process.toString() + "." + pageNumber;
    }
}
