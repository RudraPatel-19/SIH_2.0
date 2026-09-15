with open('app/src/test/java/com/example/ExampleRobolectricTest.kt', 'r') as f:
    content = f.read()

content = content.replace('  }\n  }\n\n  @Test\n  fun `camera permission denied dialog', '  }\n\n  @Test\n  fun `camera permission denied dialog')

with open('app/src/test/java/com/example/ExampleRobolectricTest.kt', 'w') as f:
    f.write(content)
