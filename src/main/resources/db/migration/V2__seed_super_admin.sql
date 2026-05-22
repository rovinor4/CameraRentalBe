INSERT INTO admins (name, email, password, role)
SELECT 'Super Admin',
       'admin@camera-rental.test',
       '$2a$10$QGTrNURXW/RbmBG/hSG5Hed/N5pPfpWfQDHdX3Zle.n8og8Cnky/q',
       'super_admin'
WHERE NOT EXISTS (SELECT 1
                  FROM admins
                  WHERE email = 'admin@camera-rental.test');
