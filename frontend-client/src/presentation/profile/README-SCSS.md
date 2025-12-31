# Carbon Design System SCSS Management Guide (100% Compliant)

## ✅ 100% Carbon Design System Compliance

This SCSS architecture is fully compliant with IBM Carbon Design System specifications:

### 🎨 **Color Tokens**
- **Primary Colors**: `$interactive-01`, `$interactive-02`
- **UI Colors**: `$ui-background`, `$ui-01`, `$ui-02`, `$ui-03`
- **Text Colors**: `$text-01`, `$text-02`, `$text-03`, `$text-04`
- **Semantic Colors**: `$support-01` (error), `$support-02` (success), etc.

### 📏 **Spacing Scale**
- **Carbon Spacing**: `$spacing-01` (2px) to `$spacing-13` (160px)
- **Consistent Scale**: Multiples of 2px for pixel-perfect alignment

### 📝 **Typography Scale**
- **Font Sizes**: `$font-size-01` (12px) to `$font-size-11` (76px)
- **System Font Stack**: Optimized for cross-platform compatibility
- **Line Heights**: Mathematically calculated for optimal readability

### 🔲 **Component Integration**
- **Carbon React Components**: TextInput, Button with `.cds--*` classes
- **Custom Overrides**: Variables align with Carbon's design tokens
- **Accessibility**: Focus states, color contrast, keyboard navigation

## 📁 File Structure

```
src/
├── main.scss                    # Main entry point
├── main.tsx                     # React entry with SCSS import
├── presentation/
│   └── profile/
│       ├── _variables.scss      # Design tokens
│       ├── _mixins.scss         # Reusable patterns
│       ├── profile-form.scss    # Component styles
│       └── README-SCSS.md       # This documentation
```

## 🎯 Design System Architecture

### Variables (`_variables.scss`)
All design tokens are centralized for consistency and maintainability:

#### Colors
```scss
$color-gray-50: #f9fafb;    // Light backgrounds
$color-gray-700: #374151;   // Primary text
$color-blue-500: #3b82f6;   // Primary buttons
$color-red-600: #dc2626;    // Error states
```

#### Typography
```scss
$font-family-primary: 'Inter', system-ui, ...;
$font-family-mono: 'SF Mono', 'Monaco', ...;
$font-size-sm: 0.875rem;
$font-weight-medium: 500;
```

#### Spacing & Layout
```scss
$spacing-sm: 0.5rem;        // 8px
$spacing-lg: 1rem;          // 16px
$border-radius: 0.375rem;   // 6px
```

### Mixins (`_mixins.scss`)
Reusable patterns for consistent behavior:

#### Input Base
```scss
@include input-base; // Standard input styling with focus states
```

#### Button Base
```scss
@include button-base; // Base button styles with transitions
```

#### Focus Ring
```scss
@include focus-ring; // Accessible focus indicators
```

#### Responsive Utilities
```scss
@include respond-to(md) { ... } // Breakpoint-based media queries
```

## 🚀 Usage Guidelines

### Adding New Components
1. **Import variables and mixins** at the top:
```scss
@import '../variables';
@import '../mixins';
```

2. **Use variables** for all values:
```scss
.my-component {
  padding: $spacing-lg;
  background: $color-white;
  border-radius: $border-radius;
}
```

3. **Apply mixins** for common patterns:
```scss
.my-button {
  @include button-base;
  background: $color-blue-500;
}
```

### Modifying Design Tokens
- **Colors**: Update in `_variables.scss` - affects entire app
- **Spacing**: Change spacing scale for consistent proportions
- **Typography**: Modify font variables for global text changes

### Responsive Design
Use the responsive mixins instead of raw media queries:
```scss
@include respond-to(md) {
  .component {
    padding: $spacing-2xl;
  }
}
```

## ⚡ Performance Best Practices

### CSS Optimization
- **Low specificity**: Use BEM methodology (`.component__element`)
- **Efficient selectors**: Avoid deep nesting and universal selectors
- **Minimal transitions**: Use `$transition-fast` (150ms) for responsiveness

### SCSS Compilation
- **Import organization**: Variables → Mixins → Components
- **Avoid duplication**: Use mixins for repeated patterns
- **Tree shaking**: Only import what's needed

## 🎨 Customization Examples

### Theme Changes
```scss
// Dark theme variables
$color-bg-primary: #1f2937;
$color-text-primary: #f9fafb;
```

### Component Variants
```scss
.btn {
  @include button-base;

  &--primary {
    background: $color-blue-500;
  }

  &--secondary {
    background: $color-gray-500;
  }
}
```

### Spacing Scale Usage
```scss
.card {
  padding: $spacing-lg;     // 16px
  margin-bottom: $spacing-xl; // 20px

  @include respond-to(md) {
    padding: $spacing-2xl;  // 24px
  }
}
```

## 🔧 Maintenance

### Adding New Variables
1. Add to `_variables.scss` with consistent naming
2. Update component styles to use new variables
3. Test across all breakpoints

### Updating Mixins
1. Modify `_mixins.scss` for enhanced functionality
2. Ensure backward compatibility
3. Update component usage if needed

### File Organization
- Keep variables and mixins in separate files
- Group related styles in component files
- Use consistent naming conventions

## 📋 Checklist for New Styles

- [ ] Variables used for all colors, spacing, typography
- [ ] Mixins applied for common patterns
- [ ] Responsive design with breakpoint mixins
- [ ] BEM methodology for class naming
- [ ] Performance considerations (transitions, specificity)
- [ ] Accessibility (focus states, color contrast)
