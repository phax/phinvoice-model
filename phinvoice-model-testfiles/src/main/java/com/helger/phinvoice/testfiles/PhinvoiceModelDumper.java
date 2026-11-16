/*
 * Copyright (C) 2026 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.phinvoice.testfiles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.ICommonsList;

/**
 * Turn a model object into a list of <code>path = value</code> lines, so that two models can be
 * compared in tests, e.g. before and after a round trip through a writer and a reader. All public
 * getters (<code>getX</code>) and list accessors (<code>x ()</code> returning a {@link List}) of the
 * classes in the model package are followed. Numbers are compared without trailing zeros.
 *
 * @author Philip Helger
 */
public final class PhinvoiceModelDumper
{
  private static final String MODEL_PACKAGE = "com.helger.phinvoice.model";

  private PhinvoiceModelDumper ()
  {}

  private static boolean _isModelClass (@NonNull final Class <?> aClass)
  {
    return aClass.getPackage () != null && MODEL_PACKAGE.equals (aClass.getPackage ().getName ());
  }

  private static void _dumpRecursive (@NonNull final String sPath,
                                      @Nullable final Object aValue,
                                      @NonNull final Set <String> aIgnoredProperties,
                                      @NonNull final ICommonsList <String> aLines)
  {
    if (aValue == null)
      return;

    if (aValue instanceof final List <?> aList)
    {
      int nIndex = 0;
      for (final Object aElement : aList)
        _dumpRecursive (sPath + "[" + nIndex++ + "]", aElement, aIgnoredProperties, aLines);
      return;
    }
    if (aValue instanceof final BigDecimal aBD)
    {
      aLines.add (sPath + " = " + aBD.stripTrailingZeros ().toPlainString ());
      return;
    }
    if (aValue instanceof final byte [] aBytes)
    {
      aLines.add (sPath + " = " + Base64.getEncoder ().encodeToString (aBytes));
      return;
    }
    if (!_isModelClass (aValue.getClass ()) || aValue.getClass ().isEnum ())
    {
      aLines.add (sPath + " = " + aValue);
      return;
    }

    final Method [] aMethods = aValue.getClass ().getMethods ();
    Arrays.sort (aMethods, Comparator.comparing (Method::getName));
    for (final Method aMethod : aMethods)
    {
      if (aMethod.getParameterCount () != 0 || Modifier.isStatic (aMethod.getModifiers ()))
        continue;
      final String sName = aMethod.getName ();
      final String sProperty;
      if (sName.startsWith ("get") && !"getClass".equals (sName))
        sProperty = Character.toLowerCase (sName.charAt (3)) + sName.substring (4);
      else
        if (List.class.isAssignableFrom (aMethod.getReturnType ()))
          sProperty = sName;
        else
          continue;
      if (aIgnoredProperties.contains (sProperty))
        continue;
      try
      {
        _dumpRecursive (sPath + "." + sProperty, aMethod.invoke (aValue), aIgnoredProperties, aLines);
      }
      catch (final IllegalAccessException | InvocationTargetException ex)
      {
        throw new IllegalStateException ("Failed to invoke " + aMethod, ex);
      }
    }
  }

  /**
   * Dump a model object.
   *
   * @param aModel
   *        The model object, e.g. an invoice. May be <code>null</code>.
   * @param aIgnoredProperties
   *        The property names to skip anywhere in the model, e.g. <code>additionalTerms</code>.
   *        May not be <code>null</code>.
   * @return The lines in a stable order. Never <code>null</code>.
   */
  @NonNull
  public static ICommonsList <String> dump (@Nullable final Object aModel, @NonNull final Set <String> aIgnoredProperties)
  {
    final ICommonsList <String> ret = new CommonsArrayList <> ();
    _dumpRecursive ("invoice", aModel, aIgnoredProperties, ret);
    return ret;
  }
}
