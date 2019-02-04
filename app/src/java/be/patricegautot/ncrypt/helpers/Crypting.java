package be.patricegautot.ncrypt.helpers;

import android.graphics.Color;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.toIntExact;

public class Crypting {

    private static final String TAG = Crypting.class.getSimpleName();

    private static boolean setup = false;
    private static Map<Character, Integer> charMap;
    private static Map<Character, Integer> charMapCheckUniqueness;
    private static char[] chars;
    private static int charDataSize;

    public static String nCrypt(String toEncrypt, String inputKey){
        dataSetup();

        String key = unique16bitKeyString(inputKey);
        //Log.e(TAG, "key for inputKey " + inputKey + " is " + key);

        String out = "";

        char m;

        int offset, startOffset, initCharId, newCharId;
        int inputSize = toEncrypt.length();
        int keySize = key.length();

        for(int i = 0; i < inputSize; i++){
            m = toEncrypt.charAt(i);

            initCharId = charMap.get(toEncrypt.charAt(i)); // gives te id of the ith character of input string

            startOffset = charMap.get(key.charAt(i%keySize)); //gives the id of the ith%keySize char of key
            offset = startOffset + i/keySize;

            newCharId = initCharId + offset;

            while(newCharId >= charDataSize){
                newCharId-=charDataSize;
            }

            out += chars[newCharId];
        }

        return out;
    }

    public static String dCrypt(String toDecrypt, String inputKey){
        dataSetup();

        String key = unique16bitKeyString(inputKey);
        //Log.e(TAG, "key for inputKey " + inputKey + " is " + key);

        String out = "";

        char m;

        int offset, startOffset, initCharId, newCharId;
        int inputSize = toDecrypt.length();
        int keySize = key.length();

        for(int i = 0; i < inputSize; i++){
            m = toDecrypt.charAt(i);

            initCharId = charMap.get(toDecrypt.charAt(i));

            startOffset =  charMap.get(key.charAt(i%keySize));
            offset = startOffset + i/keySize;

            newCharId = initCharId - offset;

            while(newCharId < 0){
                newCharId+=charDataSize;
            }

            out+= chars[newCharId];
        }

        return out;
    }

    public static int[] nCryptBitmap(int[] pixels, String inputKey){
        int pixelsLength = pixels.length;
        int[] newPixels = new int[pixelsLength];

        inputKey = genStringKeyFromKey(inputKey);

        //Log.e(TAG, "NCRYPT, input key is " + inputKey);

        long[] key = unique256bitKeyArray(inputKey);
        long keyLength = key.length;

        //Log.e(TAG, "NCRYPT key first vals : " + key[0] + " " + key[1] + " " + key[2] + " " + key[3] + " "
        //        + key[4] + " " + key[5] + " " + key[6] + " " + key[7]  + " " + key[8] + " " + key[9] + " " + key[10] + " ");

        long red, green, blue, redOffset, greenOffset, blueOffset, holderR, holderG, holderB;

        for(long i = 0; i < pixels.length; i++){
            red = Color.red(pixels[(int) i]);
            green = Color.green(pixels[(int) i]);
            blue = Color.blue(pixels[(int) i]);

            redOffset   = abs(12*key[(int) (i%keyLength)]*((i+5)/(i%3 + 1)));
            //if(i > 1) redOffset   += 6*Color.red(pixels[(int) (i/2)]);
            greenOffset = abs(34*key[(int) (i%keyLength)]*((i+6)/(i%3 + 2)));
            //if(i > 1) greenOffset += 7*Color.green(pixels[(int) (i/3)]);
            blueOffset  = abs(26*key[(int) (i%keyLength)]*((i+8)/(i%3 + 3)));
            //if(i > 1) blueOffset  += 8*Color.blue(pixels[(int) (i/4)]);

            red+=redOffset;
            green+=greenOffset;
            blue+=blueOffset;

            red = red%256;
            green = green%256;
            blue = blue%256;

            red = 255-red;
            green = 255-green;
            blue = 255-blue;

            if(i%3 == 0){
                //NOTHING
            }
            if(i%3 == 1){ // red -> blue | green -> red | blue -> green
                holderR = red;
                holderG = green;
                holderB = blue;
                red = holderB;
                green = holderR;
                blue = holderG;
            }
            if(i%3 == 2){ // red -> green | green -> blue | blue -> red
                holderR = red;
                holderG = green;
                holderB = blue;
                red = holderG;
                green = holderB;
                blue = holderR;
            }


            newPixels[(int) i] = Color.argb(255, (int) red, (int) green, (int) blue);
        }

        return newPixels;
    }


    public static int[] dCryptBitmap(int[] pixels, String inputKey){
        int pixelsLength = pixels.length;
        int[] newPixels = new int[pixelsLength];

        long colorMax = 256*256*256;

        inputKey = genStringKeyFromKey(inputKey);

        //Log.e(TAG, "DCRYPT, input key is " + inputKey);

        long[] key = unique256bitKeyArray(inputKey);
        long keyLength = key.length;

        //Log.e(TAG, "DCRYPT key first vals : " + key[0] + " " + key[1] + " " + key[2] + " " + key[3] + " "
         //       + key[4] + " " + key[5] + " " + key[6] + " " + key[7]  + " " + key[8] + " " + key[9] + " " + key[10] + " ");

        long red, green, blue, redOffset, greenOffset, blueOffset, holderR, holderG, holderB;
        for(long i = 0; i < pixels.length; i++){
            red = Color.red(pixels[(int) i]);
            green = Color.green(pixels[(int) i]);
            blue = Color.blue(pixels[(int) i]);

            if(i%3 == 0){
                //NOTHING
            }
            if(i%3 == 1){ // red <- blue | green <- red | blue <- green
                holderR = red;
                holderG = green;
                holderB = blue;
                red = holderG;
                green = holderB;
                blue = holderR;
            }
            if(i%3 == 2){ // red <- green | green <- blue | blue <- red
                holderR = red;
                holderG = green;
                holderB = blue;
                red = holderB;
                green = holderR;
                blue = holderG;
            }

            red = 255-red;
            green = 255-green;
            blue = 255-blue;

            redOffset   = abs(12*key[(int) (i%keyLength)]*((i+5)/(i%3 + 1)));
            //if(i > 1) redOffset   += 6*Color.red(newPixels[(int) (i/2)]);
            greenOffset = abs(34*key[(int) (i%keyLength)]*((i+6)/(i%3 + 2)));
            //if(i > 1) greenOffset += 7*Color.green(newPixels[(int) (i/3)]);
            blueOffset  = abs(26*key[(int) (i%keyLength)]*((i+8)/(i%3 + 3)));
            //if(i > 1) blueOffset  += 8*Color.blue(newPixels[(int) (i/4)]);

            if(red-redOffset<0) red = 256-(abs(red-redOffset)%256);
            else red-=redOffset;

            if(green-greenOffset<0) green = 256-(abs(green-greenOffset)%256);
            else green-=greenOffset;

            if(blue-blueOffset<0) blue = 256-(abs(blue-blueOffset)%256);
            else blue-=blueOffset;

            newPixels[(int) i] = Color.argb(255, (int) red, (int) green, (int) blue);
        }

        return newPixels;

    }

    private static String genStringKeyFromKey(String inputKey) {
        long score = sumOfString(inputKey);
        String newKey = "";
        for(int i = 0; i < inputKey.length(); i++){
            newKey += (char) (score%(50+2*i));
        }
        return newKey;
    }

    private static long sumOfString(String inputKey) {
        long val = 0;
        for(int i = 0; i < inputKey.length(); i++){
            val += (long) (inputKey.charAt(i));
        }
        return val;
    }

    public static long getColorOffsetFromId(long id){
        if(id%200 < 50){
            return 0;
        }
        if(id%200 >= 50 &&  id%200 < 100){
            return 64;
        }
        if(id%200 >= 100 && id%200 < 150){
            return 128;
        }
        if(id%200 >= 150){
            return 192;
        }
        return 45;
    }

    private static String unique16bitKeyString(String inputKey) {
        dataSetup();

        short sz = 16, shortMaxVal = 32767;
        String out = "";
        int keyLength = inputKey.length();

        short[] nums = new short[sz];
        for(int i = 0; i < sz; i++){
            nums[i] = (short) (((7*i+40)*(7*i+40)) % shortMaxVal);
        }

        for(int i = 0; i < 8*keyLength; i++){
            if(i >= 4*keyLength){
                if(i%sz > 0) nums[i%sz] = (short) ( ( (nums[i%sz] * nums[i%sz - 1]) + ( ( (short) inputKey.charAt(i%keyLength) ) * ( (short) inputKey.charAt(i%keyLength) ) ) ) %shortMaxVal);
                else nums[i%sz] = (short) ( ( (nums[i%sz] * nums[sz - 1]) + ( ( (short) inputKey.charAt(i%keyLength) ) * ( (short) inputKey.charAt(i%keyLength) ) ) ) %shortMaxVal);
            }
            else{
                if(i%sz > 0) nums[i%sz] = (short) ( ( (nums[i%sz] * nums[i%sz - 1]) + ( (short) inputKey.charAt(i%keyLength) ) ) %shortMaxVal);
                else nums[i%sz] = (short) ( ( (nums[i%sz] * nums[sz-1]) + ( (short) inputKey.charAt(i%keyLength) ) ) %shortMaxVal);
            }
        }

        for(int i = 0; i < sz; i++){
            out += chars[nums[i]%charDataSize];
        }

        return out;
    }

    private static long[] unique256bitKeyArray(String inputKey) {
        dataSetup();

        long sz = 256, longMaxVal = Long.MAX_VALUE;
        String out = "";
        int keyLength = inputKey.length();
        long[] key = new long[keyLength];
        for(int i = 0; i < keyLength; i++){
            key[i] = (long) (inputKey.charAt(i));
        }

        long[] nums = new long[(int)(sz)];
        for(int i = 0; i < sz; i++){
            nums[i] = (long) (((7*i+40)*(7*i+40)) % longMaxVal);
        }

        nums[0] += 2*key[0];
        for(int j = 0; j < 2; j++) {
            for (int i = 0; i < 64 * keyLength; i++) {
                nums[(int) (i % sz)] += (nums[(int) ((i / 2) % sz)] + i + i * i + key[i % keyLength] + key[(i % keyLength) / 2] + key[((i+i/2) % keyLength)]) % (4 * i + 17000000);
                nums[(int) ((j+i+2) % sz)] += (j+i+3)*(j+2)*(i/4) + key[(int) ((j+i+5) % keyLength)]*5;

                nums[(int) (i%sz)]%=17000000;
            }
        }
        return nums;
    }


    private static long genRandom(int min, int max){
        Random rand = new Random();

        int  n = rand.nextInt( (max-min+1) ) + min;
        return n;
    }

    private static void dataSetup() {
        if(setup) return;

        //Log.e(TAG, "Setting data up");

        setup = true;

        chars = new char[] {'.', 'w', 'L', '9', '(', 'W', 'l', ':', '8', 'a', 'B', ';', '7', 'b', 'A', '!', '*',
                '6', 'x', 'M', 'm', 'X', '5', 'y', ')', 'N', '4', '%', 'c', 'D', '1', 'C', '+', 'd', '3', 'z', '?',
                'O', '2', '@', 'o', 'Z', 'p', 'E', 'q', '=', 'H', 'r', 'G', ',', 'u', '-', 'F', 't', 'I', '\'', 's',
                'J', 'v', '/', 'K', ' ', 'e', 'P', 'f', 'Q', '#', 'g', 'R', '^', 'h', 'S', 'i', 'T', '\"', 'j', '0',
                'U', 'k', 'V', 'n', '_', 'Y', '\n', '&', '’', '–', '$', '£', '€', 'é', 'è', 'à', '<', '>'};

        charDataSize = chars.length;

        charMap = new HashMap<>();
        charMapCheckUniqueness = new HashMap<>();



        for(int i = 0; i < charDataSize; i++){
            if(charMapCheckUniqueness.containsKey(chars[i])){
                //Log.e(TAG, "twice char " + chars[i]);
            } else {
                charMapCheckUniqueness.put(chars[i], 1);
            }

            charMap.put(chars[i], i);
        }

        //Log.e(TAG, "Data is all set up");
    }

    public static String illegalCharacters(String in){
        dataSetup();

        String out = "";

        for(int i = 0; i < in.length(); i++){
            if(!charMap.containsKey(in.charAt(i))){
                out+=in.charAt(i) + " ";
            }
        }

        return out;
    }

    public static long modInverseCoPrimes(long k, long z, long m){  // k*x congruent to z mod m
        z%=m;
        if(z%k == 0) return z/k;
        else return gcd(k, m)[1];
    }

    private static long[] gcd(long p, long q) {
        if (q == 0)
            return new long[] { p, 1, 0 };

        long[] vals = gcd(q, p % q);
        long d = vals[0];
        long a = vals[2];
        long b = vals[1] - (p / q) * vals[2];
        return new long[] { d, a, b };
    }

    private static long pow(long n, long p){
        long val = 1;
        for(int i = 0; i < p; i++) val*=n;
        return val;
    }

}
