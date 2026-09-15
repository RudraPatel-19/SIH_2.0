import re

def fix_kisancard(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # Change elevation type to CardElevation
    content = content.replace('elevation: Dp = KisanCardDefaults.Elevation', 'elevation: CardElevation = KisanCardDefaults.cardElevation()')
    
    # In the body of KisanCard, we were doing:
    # val cardElevation = KisanCardDefaults.cardElevation(defaultElevation = elevation, ...)
    # If elevation is already a CardElevation, we can just pass it directly to Card(..., elevation = elevation)
    # Let's just remove the internal cardElevation variable
    
    content = re.sub(
        r'val cardElevation = KisanCardDefaults\.cardElevation\(\s*defaultElevation = elevation,\s*pressedElevation = elevation \+ 2\.dp\s*\)',
        '',
        content
    )
    
    content = content.replace('elevation = cardElevation', 'elevation = elevation')

    with open(filepath, 'w') as f:
        f.write(content)

fix_kisancard('app/src/main/java/com/example/presentation/components/KisanCard.kt')

def fix_appcomponents(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    content = content.replace('com.example.ui.theme.Dimens', 'com.example.presentation.theme.Dimens')
    content = content.replace('com.example.ui.theme.KisanSageLight', 'com.example.presentation.theme.KisanSurfaceVariant')
    content = content.replace('KisanSageLight', 'KisanSurfaceVariant')
    
    # Also in AppComponents: Dimens.SpacingSmall, Dimens.SpacingMedium
    # In presentation/theme/Dimens.kt, it's Dimens.spaceSmall, Dimens.spaceMedium
    # And ButtonHeight is buttonHeight
    
    content = content.replace('Dimens.CornerRadiusLarge', 'Dimens.cornerLarge')
    content = content.replace('Dimens.CornerRadiusMedium', 'Dimens.cornerMedium')
    content = content.replace('Dimens.CornerRadiusSmall', 'Dimens.cornerSmall')
    content = content.replace('Dimens.ButtonHeight', 'Dimens.buttonHeight')
    content = content.replace('Dimens.SpacingSmall', 'Dimens.spaceSmall')
    content = content.replace('Dimens.SpacingMedium', 'Dimens.spaceMedium')
    content = content.replace('Dimens.SpacingLarge', 'Dimens.spaceLarge')
    content = content.replace('Dimens.SpacingExtraSmall', 'Dimens.spaceExtraSmall')
    content = content.replace('Dimens.SpacingHuge', 'Dimens.spaceHuge')
    content = content.replace('Dimens.IconSizeMedium', 'Dimens.iconMedium')
    content = content.replace('Dimens.CardBorderWidth', '1.dp')

    with open(filepath, 'w') as f:
        f.write(content)

fix_appcomponents('app/src/main/java/com/example/ui/components/AppComponents.kt')

def fix_bottombar(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    content = content.replace('elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)', 'elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)')

    with open(filepath, 'w') as f:
        f.write(content)

fix_bottombar('app/src/main/java/com/example/ui/components/KisanBottomBar.kt')

