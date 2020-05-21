/**
 * FishersExact.java
 */

package com.sciome.bmdexpress2.util.stat;

public class FishersExact {
    private int sn11,sn1_,sn_1,sn;
    private double left, right, twotail, sleft, sright,
                   sless, slarg, sprob;

    public FishersExact() {
    }

    public FishersExact(int n00, int n01, int n10, int n11) {
        exact22(n00, n01, n10, n11);
    }

    // Reference: "Lanczos, C. 'A precision approximation
    // of the gamma function', J. SIAM Numer. Anal., B, 1, 86-96, 1964."
    // Translation of  Alan Miller's FORTRAN-implementation
    // See http://lib.stat.cmu.edu/apstat/245
    private double lngamm( int z) {
        double x = 0;
        x += 0.1659470187408462e-06/(z+7);
        x += 0.9934937113930748e-05/(z+6);
        x -= 0.1385710331296526    /(z+5);
        x += 12.50734324009056     /(z+4);
        x -= 176.6150291498386     /(z+3);
        x += 771.3234287757674     /(z+2);
        x -= 1259.139216722289     /(z+1);
        x += 676.5203681218835     /(z);
        x += 0.9999999999995183;

        double gam = (Math.log(x)-5.58106146679532777-z+(z-0.5)*Math.log(z+6.5));
        return gam;
    }

    private double lnfact(int n) {
        if(n<=1) return(0);

        return lngamm(n + 1);
    }

    private double lnbico(int n,int k) {
        return(lnfact(n) - lnfact(k) - lnfact(n-k));
    }

    private double hyper_323(int n11, int n1_, int n_1, int n) {
        return(Math.exp(lnbico(n1_,n11)+lnbico(n-n1_,n_1-n11)-lnbico(n,n_1)));
    }

    private double hyper(int n11) {
      return hyper0(n11,0,0,0);
    }

    private double hyper0(int n11i, int n1_i, int n_1i, int ni) {
        if((n1_i|n_1i|ni) == 0) {
            if(!(n11i % 10 == 0)) {
                if(n11i==sn11+1) {
                    sprob *= (((double)(sn1_-sn11))/(n11i))*(((double)(sn_1-sn11))/(n11i+sn-sn1_-sn_1));
                    sn11 = n11i;
                    return sprob;
                }

                if(n11i==sn11-1) {
                    sprob *= (((double)(sn11))/(sn1_-n11i))*(((double)(sn11+sn-sn1_-sn_1))/(sn_1-n11i));
                    sn11 = n11i;
                    return sprob;
                }
            }

            sn11 = n11i;
        } else {
            sn11 = n11i;
            sn1_=n1_i;
            sn_1=n_1i;
            sn=ni;
        }

        sprob = hyper_323(sn11,sn1_,sn_1,sn);

        return sprob;
    }

    private double exact(int n11, int n1_, int n_1, int n) {
        int i, j;
        double p, prob;

        int max=n1_;
        if(n_1 < max) max = n_1;
        int min = n1_ + n_1 - n;
        if(min < 0) min = 0;

        if(min==max) {
            sless = 1;
            sright= 1;
            sleft = 1;
            slarg = 1;

            return 1;
        }

        prob = hyper0(n11, n1_, n_1, n);
        sleft = 0;
        p = hyper(min);

        for(i = min + 1; p < 0.99999999 * prob; i++) {
          sleft += p;
          p = hyper(i);
        }

        i--;

        if (p < 1.00000001 * prob) {
            sleft += p;
        } else {
            i--;
        }

        sright = 0;
        p = hyper(max);

        for(j=max-1; p<0.99999999*prob; j--) {
            sright += p;
            p = hyper(j);
        }

        j++;

        if (p < 1.00000001 * prob) {
            sright += p;
        } else {
            j++;
        }

        if(Math.abs(i - n11) < Math.abs(j - n11)) {
            sless = sleft;
            slarg = 1 - sleft + prob;
        } else {
            sless = 1 - sright + prob;
            slarg = sright;
        }

        return prob;
    }

    private void exact22(int n11, int n12, int n21, int n22) {
        int n1_ = n11+n12;
        int n_1 = n11+n21;
        int n   = n11 +n12 +n21 +n22;
        double prob = exact(n11,n1_,n_1,n);
        left    = sless;
        right   = slarg;
        twotail = sleft+sright;
        if(twotail > 1) twotail = 1;
    }

    public double pLeft() {
        return left;
    }

    public double pRight() {
        return right;
    }

    public double twoTail() {
        return twotail;
    }

    public static void main(String[] ARGV) {
        int max = 4;
        if (ARGV.length >= max) {
            int[] cnts = new int[max];
            for (int i = 0; i < max; i++) {
                cnts[i] = Integer.parseInt(ARGV[i]);

                if (cnts[i] < 0) {
                    cnts[i] = 0;
                }
            }

            FishersExact fish = new FishersExact(cnts[0], cnts[1], cnts[2], cnts[3]);
            String newline="\n";

            System.out.println(
            " TABLE = [ " +
            cnts[0]+" , "+
            cnts[1]+" , "+
            cnts[2]+" , "+
            cnts[3]+" ]" + newline +
            "Left   : p-value = "+ fish.pLeft() + newline +
            "Right  : p-value = "+ fish.pRight() + newline +
            "2-Tail : p-value = "+ fish.twoTail() +
            newline +   "------------------------------------------");
        } else {
            System.out.println("Arguments: " + ARGV.length);
        }
    }
}
