# maglass
Easy to use magnifying tool for Java/Swing applications. Requires Java 1.7 or later.
All you need to do is to wrap content of your `JFrame` (or `JPanel`) with `JLayer` instance and add it into the `JFrame`'s content pane instead if the original component. Something like this:

```
final JLayer<Component> layer = new JLayer<>(createContent(), new MaglassUI<>()); {
  configureMagnifier((MaglassUI<?>) layer.getUI());
}
getContentPane().add(layer);
```
See maglass-demo for further details.
Your comments and suggestions are welcome ...
