package com.yum.kfc.brand.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * utilities regarding collections: some self wrote methods, all methods from 
 * CollectionUtil in apache commons-collections
 * 
 * <p>==================
 * <p>Provides utility methods and decorators for {@link Collection} instances.
 * <p>==================
 * 
 * more utils from apache commons-collections are 
 *  {@link org.apache.commons.collections.BagUtils},
 *  {@link org.apache.commons.collections.BufferUtils},
 *  {@link org.apache.commons.collections.ClosureUtils},
 *  {@link org.apache.commons.collections.ComparatorUtils},
 *  {@link org.apache.commons.collections.EnumerationUtils},
 *  {@link org.apache.commons.collections.IteratorUtils},
 *  {@link org.apache.commons.collections.ListUtils},
 *  {@link org.apache.commons.collections.MapUtils},
 *  {@link org.apache.commons.collections.SetUtils}
 * @author DING Weimin (wei-min.ding@hp.com)
 * @version
 *  1.0		Oct 28, 2009	DING Weimin (wei-min.ding@hp.com)	initial version<br>
 *  1.1		Nov 1, 2009		DING Weimin (wei-min.ding@hp.com)	extend from org.apache.commons.collections.CollectionUtils<br>
 *  1.2		Nov 2, 2009		DING Weimin (wei-min.ding@hp.com)	add toList() and contains()<br>
 *
 */
public class CollectionUtil extends CollectionUtils{

	public static <T> T peek(final List<T> list){
		if(list!=null && list.size()>0) return list.get(0);
		return null;
	}
	
	public static <T> List<T> toList(final T[] array){
		if(array==null) return null;
		List<T> lst = new ArrayList<T>(array.length);
		addAll(lst, array);
		return lst;
	}

	public static <T> boolean contains(final T[] array, T element){
		if(array==null) return false;
		
		if(element==null){
			for(T item: array){
				if (item==null) return true;
			}
		}
		else{
			for(T item: array){
				if(element.equals(item)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean containsIgnoreCase(final String[] array, String element){
		if(array==null) return false;
		
		if(element==null){
			for(String item: array){
				if (item==null) return true;
			}
		}
		else{
			for(String item: array){
				if(element.equalsIgnoreCase(item)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static <T> List<T> expandToSize(List<T> lst, int newSize){
		return expandToSize(lst, newSize, null);
	}
	
	public static <T> List<T> expandToSize(List<T> lst, int newSize, T newItem){
		if (lst == null) lst = new ArrayList<T>(newSize);
		while(lst.size() < newSize) {
			lst.add(newItem);
		}
		return lst;
	}
	
	public static <T> List<T> fillAll(List<T> lst){
		return fillAll(lst, null);
	}
	
	public static <T> List<T> fillAll(List<T> lst, T newItem){
		if (lst==null) return null;
		for(int i=0; i<lst.size(); i++){
			lst.set(i, newItem);
		}
		return lst;
	}
}
