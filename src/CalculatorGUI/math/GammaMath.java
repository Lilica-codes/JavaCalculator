package calculatorGUI.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.Supplier;

import calculatorGUI.state.AngleMode;

public class GammaMath extends BasicMath{

    public GammaMath(MathContext mc, Supplier<AngleMode> angleModeSupplier) {
        super(mc, angleModeSupplier);
    }
    


    public BigDecimal factorial(BigDecimal n) {
        return gamma(n.add(BigDecimal.ONE));
    }

    public BigDecimal gamma(BigDecimal z) {

        // 反射公式
        if (z.compareTo(new BigDecimal("0.5")) < 0) {
            return gammaReflect(z);
        }

        // Lanczos
        if (z.compareTo(new BigDecimal(20)) < 0) {
            return gammaLanczos(z);
        }

        // Stirling
        return gammaStirling(z);
    }

    private BigDecimal gammaReflect(BigDecimal z) {
    	System.out.println("here");
        BigDecimal pi = new BigDecimal(Math.PI, MC);

       
        BigDecimal sinZ = new AngleMath(MC, angleModeSupplier).sinRadFixed(pi.multiply(z));

        // 反射公式 Γ(z) = π / (sin(πz) * Γ(1−z))
        BigDecimal oneMinusZ = BigDecimal.ONE.subtract(z);

        
        BigDecimal gammaPart = gammaLanczos(oneMinusZ);

        return pi.divide(sinZ.multiply(gammaPart, MC), MC);
    }

    private BigDecimal gammaLanczos(BigDecimal z) {

    	
        double[] p = {
                0.99999999999980993,
                676.5203681218851,
                -1259.1392167224028,
                771.32342877765313,
                -176.61502916214059,
                12.507343278686905,
                -0.13857109526572012,
                9.9843695780195716e-6,
                1.5056327351493116e-7
            };

            BigDecimal x = new BigDecimal(p[0], MC);
            BigDecimal g = new BigDecimal(7, MC);
            BigDecimal t = z.subtract(BigDecimal.ONE);

            for (int i = 1; i < p.length; i++) {
                BigDecimal denom = t.add(new BigDecimal(i, MC));
                x = x.add(new BigDecimal(p[i], MC).divide(denom, MC));
            }

            BigDecimal tmp = t.add(g).add(new BigDecimal("0.5"));
            BigDecimal sqrtTwoPi = new BigDecimal(Math.sqrt(2 * Math.PI), MC);
        	BigDecimal exponent = z.subtract(new BigDecimal("0.5"));
        	BigDecimal powTerm = powBD(tmp, exponent);

        	return sqrtTwoPi
        	        .multiply(powTerm, MC)
        	        .multiply(exp(tmp.negate()), MC)
        	        .multiply(x, MC);
       
    }

    private BigDecimal gammaStirling(BigDecimal z) {
        BigDecimal twoPi = new BigDecimal(2 * Math.PI, MC);
        BigDecimal sqrtTerm = new BigDecimal(Math.sqrt(twoPi.multiply(z).doubleValue()), MC);

        BigDecimal zOverE = z.divide(new BigDecimal(Math.E, MC), MC);
        BigDecimal powTerm = powBD(zOverE, z);

        BigDecimal correction =
                BigDecimal.ONE
                .add(BigDecimal.ONE.divide(z.multiply(new BigDecimal(12), MC), MC))
                .add(BigDecimal.ONE.divide(z.pow(2).multiply(new BigDecimal(288), MC), MC));

        return sqrtTerm.multiply(powTerm, MC).multiply(correction, MC);
        
    }

}
