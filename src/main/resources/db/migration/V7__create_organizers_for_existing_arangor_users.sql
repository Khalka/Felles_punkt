-- Create Organizer entries for all existing ARANGOR users who don't have one yet
INSERT INTO public.Organizer (name, email, phone)
SELECT 
    CONCAT(u.firstName, ' ', u.lastName) as name,
    u.mailaddress as email,
    u.telephone as phone
FROM public.Users u
WHERE u.role = 'ARANGOR'
AND NOT EXISTS (
    SELECT 1 FROM public.Organizer o WHERE o.email = u.mailaddress
);
