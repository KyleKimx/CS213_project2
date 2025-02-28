package rubank.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class List<E> implements Iterable<E> {
    private E[] objects;
    private int size;

    private static final int GROWTH = 4;
    
    @SuppressWarnings("unchecked")
    public List() { 
        this.objects = (E[]) new Object[GROWTH];
        this.size = 0;
    } 
    
    private int find(E e) {
        for (int i = 0; i<size; i++){
            if(objects[i].equals(e)){
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private void grow() {
        E[] newArray = (E[]) new Object[objects.length + GROWTH];
        for(int i = 0; i<size; i++){
            newArray[i] = objects[i];
        }
        objects = newArray; 
    }
    
    public boolean contains(E e) {
        return (find(e) != -1);
    }
    
    public void add(E e) {
        // if (contains(e)) {
        //     return;
        // }
        if (size == objects.length) {
            grow();
        }
        objects[size++] = e;
    }
    public void remove(E e) {
        int idx = find(e);
        if (idx == -1) return;
        for (int i = idx; i < size - 1; i++) {
            objects[i] = objects[i + 1];
        }
        objects[--size] = null;
    }

    public boolean isEmpty() {
        return size == 0;
    }
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return new ListIterator<E>();
    } //for traversing the list

    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("List index out of range.");
        }
        return objects[index];
    } //return the object at the index

    public void set(int index, E e) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("List index out of range.");
        }
        objects[index] = e;
    } //put object e at the index

    public int indexOf(E e) {
        return find(e);
    } //return the index of the object or return -1
    
    private class ListIterator<E> implements Iterator<E> {
        int current = 0; //current index when traversing the list (array)
        
        @Override
        public boolean hasNext(){
            return current < size;
        } //false if it’s empty or at end of list
        
        @Override
        public E next(){
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return (E) objects[current++];
        } //return the next object in the list
    } 
}