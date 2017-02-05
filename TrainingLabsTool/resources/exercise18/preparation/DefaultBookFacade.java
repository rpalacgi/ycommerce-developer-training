/*
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package my.bookstore.facades.book.impl;

import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.LinkedList;
import java.util.List;

import my.bookstore.core.model.BookModel;
import my.bookstore.core.services.BookService;
import my.bookstore.facades.book.BookFacade;
import my.bookstore.facades.product.data.BookData;



public class DefaultBookFacade implements BookFacade
{

	// References to the services we'll be calling: BookService and a Converter

	private BookService bookService;
	private Converter<BookModel, BookData> bookConverter;


	// Purpose of this class: get trending books
	@Override
	public List<BookData> getTrendingBooks(final int numberOfBooks)
	{
		final List<BookData> bookDTOs = new LinkedList<>();
		/* TODO exercise 18.1: add your code here: */


		return bookDTOs;
	}


	// Accessor methods for BookService and Converter
	
	public BookService getBookService()
	{ return bookService; }

	public void setBookService(final BookService bookService)
	{ this.bookService = bookService; }

	public Converter<BookModel, BookData> getBookConverter()
	{ return bookConverter; }

	public void setBookConverter(final Converter<BookModel, BookData> bookConverter)
	{ this.bookConverter = bookConverter; }
	
}
