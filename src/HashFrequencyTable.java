/**
 * Created by kylenosar on 10/17/14.
 */

import java.io.*;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.PrintStream;
public class HashFrequencyTable<K> implements FrequencyTable<K>, Iterable<K>
{


    ArrayList<Entry> table; // the table to store the words in
    public float maxLoadFactor; //load factor decided by user. mlf = this / table.size()
    private int numberOfEntries; // the number of entries in the table

    public HashFrequencyTable(int initialCapacity, float maxLoadFactor) {
        this.maxLoadFactor = maxLoadFactor; // set max load factor
        int sz = nextPowerOfTwo(initialCapacity); // sets size to the next power of 2
        table = new ArrayList<Entry>(sz);
        for (int i = 0; i < sz; i++) {
            table.add(null);
        }
        numberOfEntries = 0;
    }


    private class Entry
    {
        public K key; // each entry contains a key
        public int count; // count is initially 1 if its in the tree, for copy case count increments by one
        public Entry(K k) {key=k; count=1;}
    }

    private class TableIterator implements Iterator<K> {
        private int i;
        public TableIterator() {i = 0;}

        public boolean hasNext() {
            while (i < table.size() && table.get(i) == null)
                i++;
            return i < table.size();
        }

        public K next() {
            return table.get(i++).key;
        }
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }
    }

    public Iterator<K> iterator() {
        return new TableIterator();
    }


    @Override
    public void click(K key) {
        int hashVal = key.hashCode() % table.size();
        int i = 0;
        if (hashVal < 0) hashVal += table.size();
        int k = hashVal;
        Entry e;
        while ((e = table.get(k)) != null) {

            if (key.equals(e.key)) {
                e.key = key;
                e.count +=1;
                return;
            }

            i++;
            k =  (hashVal + (i * (i+1))/2) % (table.size());
        }
        e = new Entry(key);
        table.set(k, e);
        numberOfEntries++;

        if (loadFactor() > this.maxLoadFactor) {
            Rehash();
        }
    }

    @Override
    public int count(K key) {
        int i = 0;
        int hashVal = key.hashCode() % table.size();
        if (hashVal < 0) hashVal += table.size();
        int k = hashVal;
        Entry e;
        while ((e = table.get(k)) != null)
        {
            if (key.equals(e.key)) //TODO: left off here
                return e.count; // hit
            i++;
            k =  (hashVal + (i * (i+1))/2) % (table.size());
        }
        return 0; //missed and never found
    }

    private float loadFactor() { return (float) numberOfEntries / table.size(); }

    private static int nextPowerOfTwo(int n) {
        int e = 1;
        while ((1 << e) < n)
            e++;
        return 1 << e;
    }

    //this function goes to the next power of two in size(doubles) and rehashes the table
    private void Rehash() {
        ArrayList<Entry> oldTable = table;
        int n = nextPowerOfTwo(table.size()+1);
        table = new ArrayList<Entry>(n);
        for (int i = 0; i < n; i++) {
            table.add(null);
        }

        numberOfEntries = 0;

        for (int i = 0; i < oldTable.size(); i++) {
            Entry e = oldTable.get(i);
            while (e != null && e.count > 1) {
                click(e.key);
                e.count -=1;
            }

            if (e != null) {
                click(e.key);
            }
        }
    }


     public void dump(PrintStream str) {
         int i = 0;

         for (Entry e : table) {
             if(e != null) {
                 String k = (String)e.key;
                 int c = e.count;
                 str.println(i + ": key = '" + k + "', count = " + c);
                 i++;
             } else {
                 str.println(i +": " + e);
                 i++;
             }
         }
     }

    /* TEST MAIN
    public static void main(String[] args) {
        String hamlet =
                "To be or not to be that is the question " +
                        "Whether 'tis nobler in the mind to suffer " +
                        "The slings and arrows of outrageous fortune ";
        String words[] = hamlet.split("\\s+");
        HashFrequencyTable<String> table = new HashFrequencyTable<String>(10, 0.95F);
        for (int i = 0; i < words.length; i++)
            if (words[i].length() > 0)
                table.click(words[i]);
        table.dump(System.out);
    }
    */
}

