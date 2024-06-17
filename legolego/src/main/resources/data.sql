INSERT INTO DIY_package (user_num, package_name, profile_img, reg_date, mod_date, package_liked_num, package_view_num, short_description)
VALUES (1, 'Sample Package', 'https://example.com/sample_image.jpg', CURRENT_TIMESTAMP, NULL, 0, 0, 'This is a sample DIY package description.');

 INSERT INTO admin (admin_email, admin_pw, admin_name, admin_aproval)
VALUES ('admin@example.com', 'securepassword', 'Admin Name', 'false');

insert into product values('가가가','https://example.com/product_image1.jpg','99.99','2024-12-31','false','0','0');