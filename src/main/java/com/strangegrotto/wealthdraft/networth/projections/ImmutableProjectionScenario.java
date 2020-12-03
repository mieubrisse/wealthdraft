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

import com.strangegrotto.wealthdraft.networth.projections.raw.RawAssetChange;
import org.immutables.value.Generated;

/**
 * Immutable implementation of {@link ProjectionScenario}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableProjectionScenario.builder()}.
 */
@Generated(from = "ProjectionScenario", generator = "Immutables")
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@javax.annotation.processing.Generated("org.immutables.processor.ProxyProcessor")
@Immutable
@CheckReturnValue
public final class ImmutableProjectionScenario
    implements ProjectionScenario {
  private final String name;
  private final ImmutableMap<String, Map<String, RawAssetChange>> changes;

  private ImmutableProjectionScenario(
      String name,
      ImmutableMap<String, Map<String, RawAssetChange>> changes) {
    this.name = name;
    this.changes = changes;
  }

  /**
   * @return The value of the {@code name} attribute
   */
  @JsonProperty("name")
  @Override
  public String getName() {
    return name;
  }

  /**
   * @return The value of the {@code changes} attribute
   */
  @JsonProperty("changes")
  @Override
  public ImmutableMap<String, Map<String, RawAssetChange>> getChanges() {
    return changes;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link ProjectionScenario#getName() name} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for name
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableProjectionScenario withName(String value) {
    String newValue = Objects.requireNonNull(value, "name");
    if (this.name.equals(newValue)) return this;
    return new ImmutableProjectionScenario(newValue, this.changes);
  }

  /**
   * Copy the current immutable object by replacing the {@link ProjectionScenario#getChanges() changes} map with the specified map.
   * Nulls are not permitted as keys or values.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param entries The entries to be added to the changes map
   * @return A modified copy of {@code this} object
   */
  public final ImmutableProjectionScenario withChanges(Map<String, ? extends Map<String, RawAssetChange>> entries) {
    if (this.changes == entries) return this;
    ImmutableMap<String, Map<String, RawAssetChange>> newValue = ImmutableMap.copyOf(entries);
    return new ImmutableProjectionScenario(this.name, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableProjectionScenario} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableProjectionScenario
        && equalTo((ImmutableProjectionScenario) another);
  }

  private boolean equalTo(ImmutableProjectionScenario another) {
    return name.equals(another.name)
        && changes.equals(another.changes);
  }

  /**
   * Computes a hash code from attributes: {@code name}, {@code changes}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    @Var int h = 5381;
    h += (h << 5) + name.hashCode();
    h += (h << 5) + changes.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code ProjectionScenario} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("ProjectionScenario")
        .omitNullValues()
        .add("name", name)
        .add("changes", changes)
        .toString();
  }

  /**
   * Utility type used to correctly read immutable object from JSON representation.
   * @deprecated Do not use this type directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Generated(from = "ProjectionScenario", generator = "Immutables")
  @Deprecated
  @SuppressWarnings("Immutable")
  @JsonDeserialize
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
  static final class Json implements ProjectionScenario {
    @Nullable String name;
    @Nullable Map<String, Map<String, RawAssetChange>> changes = ImmutableMap.of();
    @JsonProperty("name")
    public void setName(String name) {
      this.name = name;
    }
    @JsonProperty("changes")
    public void setChanges(Map<String, Map<String, RawAssetChange>> changes) {
      this.changes = changes;
    }
    @Override
    public String getName() { throw new UnsupportedOperationException(); }
    @Override
    public Map<String, Map<String, RawAssetChange>> getChanges() { throw new UnsupportedOperationException(); }
  }

  /**
   * @param json A JSON-bindable data structure
   * @return An immutable value type
   * @deprecated Do not use this method directly, it exists only for the <em>Jackson</em>-binding infrastructure
   */
  @Deprecated
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  static ImmutableProjectionScenario fromJson(Json json) {
    ImmutableProjectionScenario.Builder builder = ImmutableProjectionScenario.builder();
    if (json.name != null) {
      builder.name(json.name);
    }
    if (json.changes != null) {
      builder.putAllChanges(json.changes);
    }
    return builder.build();
  }

  /**
   * Creates an immutable copy of a {@link ProjectionScenario} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable ProjectionScenario instance
   */
  public static ImmutableProjectionScenario copyOf(ProjectionScenario instance) {
    if (instance instanceof ImmutableProjectionScenario) {
      return (ImmutableProjectionScenario) instance;
    }
    return ImmutableProjectionScenario.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableProjectionScenario ImmutableProjectionScenario}.
   * <pre>
   * ImmutableProjectionScenario.builder()
   *    .name(String) // required {@link ProjectionScenario#getName() name}
   *    .putChanges|putAllChanges(String =&gt; Map&amp;lt;String, com.strangegrotto.wealthdraft.networth.projections.AssetChange&amp;gt;) // {@link ProjectionScenario#getChanges() changes} mappings
   *    .build();
   * </pre>
   * @return A new ImmutableProjectionScenario builder
   */
  public static ImmutableProjectionScenario.Builder builder() {
    return new ImmutableProjectionScenario.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableProjectionScenario ImmutableProjectionScenario}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "ProjectionScenario", generator = "Immutables")
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_NAME = 0x1L;
    private long initBits = 0x1L;

    private @Nullable String name;
    private ImmutableMap.Builder<String, Map<String, RawAssetChange>> changes = ImmutableMap.builder();

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code ProjectionScenario} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder from(ProjectionScenario instance) {
      Objects.requireNonNull(instance, "instance");
      name(instance.getName());
      putAllChanges(instance.getChanges());
      return this;
    }

    /**
     * Initializes the value for the {@link ProjectionScenario#getName() name} attribute.
     * @param name The value for name 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    @JsonProperty("name")
    public final Builder name(String name) {
      this.name = Objects.requireNonNull(name, "name");
      initBits &= ~INIT_BIT_NAME;
      return this;
    }

    /**
     * Put one entry to the {@link ProjectionScenario#getChanges() changes} map.
     * @param key The key in the changes map
     * @param value The associated value in the changes map
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder putChanges(String key, Map<String, RawAssetChange> value) {
      this.changes.put(key, value);
      return this;
    }

    /**
     * Put one entry to the {@link ProjectionScenario#getChanges() changes} map. Nulls are not permitted
     * @param entry The key and value entry
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder putChanges(Map.Entry<String, ? extends Map<String, RawAssetChange>> entry) {
      this.changes.put(entry);
      return this;
    }

    /**
     * Sets or replaces all mappings from the specified map as entries for the {@link ProjectionScenario#getChanges() changes} map. Nulls are not permitted
     * @param entries The entries that will be added to the changes map
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    @JsonProperty("changes")
    public final Builder changes(Map<String, ? extends Map<String, RawAssetChange>> entries) {
      this.changes = ImmutableMap.builder();
      return putAllChanges(entries);
    }

    /**
     * Put all mappings from the specified map as entries to {@link ProjectionScenario#getChanges() changes} map. Nulls are not permitted
     * @param entries The entries that will be added to the changes map
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder putAllChanges(Map<String, ? extends Map<String, RawAssetChange>> entries) {
      this.changes.putAll(entries);
      return this;
    }

    /**
     * Builds a new {@link ImmutableProjectionScenario ImmutableProjectionScenario}.
     * @return An immutable instance of ProjectionScenario
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableProjectionScenario build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableProjectionScenario(name, changes.build());
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<>();
      if ((initBits & INIT_BIT_NAME) != 0) attributes.add("name");
      return "Cannot build ProjectionScenario, some of required attributes are not set " + attributes;
    }
  }
}
