package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Var;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import com.strangegrotto.wealthdraft.networth.projections.raw.RawProjections;
import org.immutables.value.Generated;

/**
 * Immutable implementation of {@link RawProjections}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableProjections.builder()}.
 */
@Generated(from = "Projections", generator = "Immutables")
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@javax.annotation.processing.Generated("org.immutables.processor.ProxyProcessor")
@Immutable
@CheckReturnValue
public final class ImmutableProjections implements RawProjections {
  private final Double defaultGrowth;
  private final ImmutableMap<String, ProjectionScenario> scenarios;

  private ImmutableProjections(
      Double defaultGrowth,
      ImmutableMap<String, ProjectionScenario> scenarios) {
    this.defaultGrowth = defaultGrowth;
    this.scenarios = scenarios;
  }

  /**
   * @return The value of the {@code defaultGrowth} attribute
   */
  @JsonProperty("defaultGrowth")
  @Override
  public Double getDefaultGrowth() {
    return defaultGrowth;
  }

  /**
   * @return The value of the {@code scenarios} attribute
   */
  @JsonProperty("scenarios")
  @Override
  public ImmutableMap<String, ProjectionScenario> getScenarios() {
    return scenarios;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link RawProjections#getDefaultGrowth() defaultGrowth} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for defaultGrowth
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableProjections withDefaultGrowth(Double value) {
    Double newValue = Objects.requireNonNull(value, "defaultGrowth");
    if (this.defaultGrowth.equals(newValue)) return this;
    return new ImmutableProjections(newValue, this.scenarios);
  }

  /**
   * Copy the current immutable object by replacing the {@link RawProjections#getScenarios() scenarios} map with the specified map.
   * Nulls are not permitted as keys or values.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param entries The entries to be added to the scenarios map
   * @return A modified copy of {@code this} object
   */
  public final ImmutableProjections withScenarios(Map<String, ? extends ProjectionScenario> entries) {
    if (this.scenarios == entries) return this;
    ImmutableMap<String, ProjectionScenario> newValue = ImmutableMap.copyOf(entries);
    return new ImmutableProjections(this.defaultGrowth, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableProjections} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableProjections
        && equalTo((ImmutableProjections) another);
  }

  private boolean equalTo(ImmutableProjections another) {
    return defaultGrowth.equals(another.defaultGrowth)
        && scenarios.equals(another.scenarios);
  }

  /**
   * Computes a hash code from attributes: {@code defaultGrowth}, {@code scenarios}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    @Var int h = 5381;
    h += (h << 5) + defaultGrowth.hashCode();
    h += (h << 5) + scenarios.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code Projections} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("Projections")
        .omitNullValues()
        .add("defaultGrowth", defaultGrowth)
        .add("scenarios", scenarios)
        .toString();
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Generated(from = "Projections", generator = "Immutables")
  @Deprecated
  @SuppressWarnings("Immutable")
  @JsonDeserialize
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
  static final class Json implements RawProjections {
    @Nullable Double defaultGrowth;
    @Nullable Map<String, ProjectionScenario> scenarios = ImmutableMap.of();
    @JsonProperty("defaultGrowth")
    public void setDefaultGrowth(Double defaultGrowth) {
      this.defaultGrowth = defaultGrowth;
    }
    @JsonProperty("scenarios")
    public void setScenarios(Map<String, ProjectionScenario> scenarios) {
      this.scenarios = scenarios;
    }
    @Override
    public Double getDefaultGrowth() { throw new UnsupportedOperationException(); }
    @Override
    public Map<String, ProjectionScenario> getScenarios() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  static ImmutableProjections fromJson(Json json) {
    ImmutableProjections.Builder builder = ImmutableProjections.builder();
    if (json.defaultGrowth != null) {
      builder.defaultGrowth(json.defaultGrowth);
    }
    if (json.scenarios != null) {
      builder.putAllScenarios(json.scenarios);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link RawProjections} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable Projections instance
   */
  public static ImmutableProjections copyOf(RawProjections instance) {
    if (instance instanceof ImmutableProjections) {
      return (ImmutableProjections) instance;
    }
    return ImmutableProjections.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableProjections ImmutableProjections}.
   * <pre>
   * ImmutableProjections.builder()
   *    .defaultGrowth(Double) // required {@link RawProjections#getDefaultGrowth() defaultGrowth}
   *    .putScenarios|putAllScenarios(String =&gt; com.strangegrotto.wealthdraft.networth.projections.ProjectionScenario) // {@link RawProjections#getScenarios() scenarios} mappings
   *    .build();
   * </pre>
   * @return A new ImmutableProjections builder
   */
  public static ImmutableProjections.Builder builder() {
    return new ImmutableProjections.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableProjections ImmutableProjections}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "Projections", generator = "Immutables")
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_DEFAULT_GROWTH = 0x1L;
    private long initBits = 0x1L;

    private @Nullable Double defaultGrowth;
    private ImmutableMap.Builder<String, ProjectionScenario> scenarios = ImmutableMap.builder();

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code Projections} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder from(RawProjections instance) {
      Objects.requireNonNull(instance, "instance");
      defaultGrowth(instance.getDefaultGrowth());
      putAllScenarios(instance.getScenarios());
      return this;
    }

    /**
     * Initializes the value for the {@link RawProjections#getDefaultGrowth() defaultGrowth} attribute.
     * @param defaultGrowth The value for defaultGrowth 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    @JsonProperty("defaultGrowth")
    public final Builder defaultGrowth(Double defaultGrowth) {
      this.defaultGrowth = Objects.requireNonNull(defaultGrowth, "defaultGrowth");
      initBits &= ~INIT_BIT_DEFAULT_GROWTH;
      return this;
    }

    /**
     * Put one entry to the {@link RawProjections#getScenarios() scenarios} map.
     * @param key The key in the scenarios map
     * @param value The associated value in the scenarios map
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder putScenarios(String key, ProjectionScenario value) {
      this.scenarios.put(key, value);
      return this;
    }

    /**
     * Put one entry to the {@link RawProjections#getScenarios() scenarios} map. Nulls are not permitted
     * @param entry The key and value entry
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder putScenarios(Map.Entry<String, ? extends ProjectionScenario> entry) {
      this.scenarios.put(entry);
      return this;
    }

    /**
     * Sets or replaces all mappings from the specified map as entries for the {@link RawProjections#getScenarios() scenarios} map. Nulls are not permitted
     * @param entries The entries that will be added to the scenarios map
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    @JsonProperty("scenarios")
    public final Builder scenarios(Map<String, ? extends ProjectionScenario> entries) {
      this.scenarios = ImmutableMap.builder();
      return putAllScenarios(entries);
    }

    /**
     * Put all mappings from the specified map as entries to {@link RawProjections#getScenarios() scenarios} map. Nulls are not permitted
     * @param entries The entries that will be added to the scenarios map
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder putAllScenarios(Map<String, ? extends ProjectionScenario> entries) {
      this.scenarios.putAll(entries);
      return this;
    }

    /**
     * Builds a new {@link ImmutableProjections ImmutableProjections}.
     * @return An immutable instance of Projections
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableProjections build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableProjections(defaultGrowth, scenarios.build());
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<>();
      if ((initBits & INIT_BIT_DEFAULT_GROWTH) != 0) attributes.add("defaultGrowth");
      return "Cannot build Projections, some of required attributes are not set " + attributes;
    }
  }
}
